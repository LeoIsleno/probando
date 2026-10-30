#!/usr/bin/env python3
"""Exporta los locales descubiertos a un CSV para contactar por WhatsApp.

Por que existe: `harvey export` escribe un CSV pensado para cargar en un
sequencer de cold email (email, first_name, title, email_status...). No trae
telefono, rating, reseñas ni si la ficha de Google esta sin reclamar, y por
defecto filtra a las direcciones verificadas, asi que sin un verifier de
email configurado devuelve cero filas. Este script lee la misma base
(data/harvey.db) y saca las columnas que se usan para prospectar a mano.

Uso:
    python3 tools/export_prospectos.py
    python3 tools/export_prospectos.py --out /ruta/al.csv --min-reseñas 5

No escribe en la base: solo lee.
"""

import argparse
import csv
import os
import sqlite3
import sys

# Un signal por columna. Harvey guarda cada dato como una fila en
# `observations`, no como una columna, asi que se pivotea aca.
SIGNALS = {
    "UNCLAIMED_LISTING": "num",   # 1.0 = la ficha esta sin reclamar
    "REVIEW_RATING": "num",       # 4.5
    "REVIEW_COUNT": "num",        # 128
    "NO_WEBSITE": "num",          # 1.0 = no tiene web
}

COLUMNAS = [
    "nombre", "localidad", "direccion", "telefono", "tiene_web", "web",
    "ficha_sin_reclamar", "rating", "resenias", "rubro", "fuente",
]


def ultimas_observaciones(con):
    """{company_id: {signal_code: valor}} tomando la observacion mas reciente.

    Un discover re-corrido vuelve a observar lo mismo, lo que da una serie de
    tiempo gratis; para el CSV interesa el ultimo valor de cada signal.
    """
    filas = con.execute(
        """
        SELECT o.company_id, o.signal_code, o.value_num, o.value_text
          FROM observations o
          JOIN (
                SELECT company_id, signal_code, MAX(observed_at) AS t
                  FROM observations
                 WHERE company_id != '' AND signal_code IN (%s)
              GROUP BY company_id, signal_code
               ) u
            ON u.company_id = o.company_id
           AND u.signal_code = o.signal_code
           AND u.t = o.observed_at
        """
        % ",".join("?" * len(SIGNALS)),
        tuple(SIGNALS),
    ).fetchall()

    out = {}
    for company_id, code, num, text in filas:
        out.setdefault(company_id, {})[code] = num if SIGNALS[code] == "num" else text
    return out


def si_no(valor):
    """Un signal presente con 1.0 es un si; ausente es desconocido, no un no."""
    if valor is None:
        return ""
    return "si" if float(valor) >= 1.0 else "no"


def numero(valor, entero=False):
    if valor is None:
        return ""
    return str(int(valor)) if entero else str(valor)


def main():
    raiz = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    ap = argparse.ArgumentParser(description=__doc__,
                                 formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--db", default=os.path.join(raiz, "data", "harvey.db"))
    ap.add_argument("--out", default=os.path.join(raiz, "prospectos.csv"))
    ap.add_argument("--con-telefono", action="store_true",
                    help="dejar afuera los locales sin telefono")
    ap.add_argument("--min-resenias", type=int, default=None,
                    help="minimo de reseñas para incluir el local")
    args = ap.parse_args()

    if not os.path.exists(args.db):
        sys.exit(f"No existe la base {args.db}. Corre `harvey discover` primero.")

    con = sqlite3.connect(f"file:{args.db}?mode=ro", uri=True)
    obs = ultimas_observaciones(con)

    empresas = con.execute(
        """SELECT id, name, phone, website, location, industry, source
             FROM companies
         ORDER BY name"""
    ).fetchall()

    filas, descartadas = [], 0
    for cid, nombre, telefono, web, localidad, rubro, fuente in empresas:
        s = obs.get(cid, {})

        if args.con_telefono and not (telefono or "").strip():
            descartadas += 1
            continue
        resenias = s.get("REVIEW_COUNT")
        if args.min_resenias is not None and (resenias or 0) < args.min_resenias:
            descartadas += 1
            continue

        # `tiene_web` se cruza con el signal NO_WEBSITE: si el provider dijo
        # explicitamente que no tiene, eso gana sobre un campo website vacio
        # (que puede ser simplemente un dato que no vino).
        if s.get("NO_WEBSITE") is not None:
            tiene_web = "no"
        elif (web or "").strip():
            tiene_web = "si"
        else:
            tiene_web = ""

        filas.append({
            "nombre": nombre or "",
            "localidad": localidad or "",
            # Harvey NO persiste la direccion: el provider la devuelve pero se
            # descarta al guardar la empresa (companies no tiene esa columna).
            "direccion": "",
            "telefono": telefono or "",
            "tiene_web": tiene_web,
            "web": web or "",
            "ficha_sin_reclamar": si_no(s.get("UNCLAIMED_LISTING")),
            "rating": numero(s.get("REVIEW_RATING")),
            "resenias": numero(resenias, entero=True),
            "rubro": rubro or "",
            "fuente": fuente or "",
        })

    with open(args.out, "w", newline="", encoding="utf-8-sig") as fh:
        w = csv.DictWriter(fh, fieldnames=COLUMNAS)
        w.writeheader()
        w.writerows(filas)

    con_tel = sum(1 for f in filas if f["telefono"])
    sin_reclamar = sum(1 for f in filas if f["ficha_sin_reclamar"] == "si")
    print(f"\n  {len(filas)} locales -> {args.out}")
    if descartadas:
        print(f"  {descartadas} descartados por los filtros que pediste")
    print(f"  con telefono: {con_tel}/{len(filas)}")
    print(f"  ficha sin reclamar: {sin_reclamar}")
    if filas and not sin_reclamar:
        print("  (si ninguno figura sin reclamar, puede que el provider no de"
              " ese dato: Scavio Maps y OpenStreetMap no lo traen)")
    print("  La columna direccion va vacia a proposito: Harvey no la guarda.\n")


if __name__ == "__main__":
    main()
