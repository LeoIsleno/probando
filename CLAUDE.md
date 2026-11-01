# Harvey — prospección de locales de celulares en Córdoba

Contexto para cualquier sesión que trabaje en este repo. Lo de abajo son
instrucciones del dueño del proyecto, no sugerencias.

> **Hay otro CLAUDE.md en `harvey/`**: es el wizard de setup del proyecto
> upstream (ethanplusai/harvey), pensado para alguien que quiere mandar cold
> email. **Este archivo gana sobre aquel.** Ignorá del wizard todo lo que
> hable de Gmail, Instantly, verificadores de email, warmup, TrulyInbox,
> autopilot o `harvey run`.

---

## 1. Qué es esto y para qué NO es

Leonardo Isleño tiene un local de venta y reparación de celulares en Córdoba,
Argentina, y desarrolló un sistema web de gestión para locales como el suyo.
Lo quiere vender como SaaS a otros locales de Córdoba.

Harvey se usa **solo como armador de listas**. El contacto lo hace Leo a mano,
por WhatsApp. El entregable es un CSV, nunca un mail.

### Reglas duras — no las toques

- **Nunca correr `harvey run`.** Ni para probar.
- **`channels.email.require_approval` queda en `true`. Siempre.**
- `channels.email.max_daily_sends` queda en `0`.
- El provider de mail es `smtp` con valores inválidos a propósito en `.env`.
- **No configurar verificador de email** (Reoon/ZeroBounce/Hunter). Sin
  verificador toda dirección queda marcada `guess` y Harvey nunca la envía.
  Es una red de seguridad deliberada, no un olvido.
- **Parar y preguntar antes de cualquier comando que gaste plata.** El único
  que gasta es `harvey discover` sin `--estimate`.
- Si algo del README de upstream no coincide con el repo, **decirlo en vez de
  improvisar**.

Son tres candados independientes: `require_approval`, `max_daily_sends: 0` y
credenciales SMTP inválidas.

---

## 2. Arrancar

```bash
cd harvey
python3.13 -m venv .venv && source .venv/bin/activate && pip install -e .
# .env está gitignoreado: no viene en el clon. Ver sección 4.
```

`harvey train` **no se usa**: el `harvey.yaml` está escrito a mano y
versionado. Correr `train` lo pisaría.

Comandos que se usan acá:

```bash
harvey signals                                  # ver catálogo de señales
harvey signals --confirm free                   # las 26 gratuitas
harvey discover --providers                     # menú con precios reales
harvey discover --provider <key> --estimate     # estimación, no gasta
harvey discover --provider <key> --max-spend N  # GASTA: preguntar antes
python3 tools/export_prospectos.py --con-telefono
```

---

## 3. Qué ya está hecho

- Harvey instalado desde upstream `4237f07`, en `harvey/`.
- `harvey.yaml` con el ICP real: 4 rubros de celulares, Córdoba capital.
  Las otras 3 ciudades (Villa Carlos Paz, Río Cuarto, Villa María) están
  comentadas en `icp.geography` para una segunda corrida.
- 26 señales gratuitas confirmadas. Las 3 que consumen crédito
  (`MAPS_RANK`, `SERP_RANK`, `EMAIL_STATUS`) quedan sin confirmar.
- Una corrida de DataForSEO Business Listings: 46 locales, $0.0583.
- `tools/export_prospectos.py`: el CSV de prospección.

---

## 4. Trampas ya descubiertas — leer antes de repetir el trabajo

### `harvey export` NO sirve para este caso
Escribe columnas de sequencer de cold email (`email`, `first_name`, `title`,
`email_status`...). **No tiene teléfono, dirección, rating, reseñas ni
ficha-sin-reclamar**, y filtra por email verificado, así que sin verificador
devuelve **cero filas**. Por eso existe `tools/export_prospectos.py`, que lee
`data/harvey.db` directo y pivotea la tabla `observations`.

### La dirección se pierde
El provider devuelve la dirección pero Harvey la descarta: `companies` no
tiene esa columna y el upsert de `discover.py` solo pasa `phone` y `location`.
La columna `direccion` del CSV sale **siempre vacía**. Arreglarlo requiere
parchear el upsert.

### Harvey guarda señales como filas, no como columnas
Cada dato es una fila en `observations` (`company_id`, `signal_code`,
`value_num`...). Para armar un CSV hay que pivotear tomando la observación más
reciente por `(company_id, signal_code)`.

### El schema de config es estricto (pydantic)
Tres campos que rompen si se escriben con la forma intuitiva:
- `persona.linkedin` → requerido, sin default. Vacío está bien, ausente no.
- `product.objection_responses` → `dict[str, str]`, **no** lista.
- `product.offer` → objeto `OfferConfig`, **no** string.

Validar siempre con `python3 -c "from harvey.config import load_config; load_config()"`.

### OpenStreetMap: parcheado y SIN PROBAR
El `TAG_MAP` de upstream es un diccionario hardcodeado de 9 oficios en inglés
(techista, plomero, dentista...) y devolvía **cero** para rubros de celulares.
Se agregaron `shop=mobile_phone` y `craft=electronics_repair`.

**Esto es un fork** sobre `harvey/harvey/collectors/discover.py`, marcado con
`# --- Agregado local (no viene de upstream) ---`. Un `git pull` de upstream va
a dar conflicto ahí.

**Nunca se probó contra datos reales**: Overpass estaba bloqueado por la red
del entorno donde se hizo el setup. En una máquina sin ese bloqueo debería
andar. Si anda y resulta útil, conviene mandarlo como PR a upstream — el
`TAG_MAP` debería ser configurable desde el yaml.

### DataForSEO
- Credenciales en `.env`: `DATAFORSEO_LOGIN` y `DATAFORSEO_PASSWORD`. La
  password es la **API password autogenerada** del panel, no la de la cuenta.
- **El sandbox (`DATAFORSEO_SANDBOX`) devuelve 403 `40104`** aunque la cuenta
  esté verificada: es un flag aparte. Producción funciona.
- Una cuenta sin verificar da `403 / 40104` en todos los endpoints de datos,
  pero `appendix/user_data` responde 200 igual. No confundir con auth rota.
- Costo real: `$0.012/query + $0.00036 por negocio`. Córdoba capital (4
  queries) estimó $0.1920 y salió $0.0583.
- La query `accesorios para celulares` falló con 403 en la corrida; las otras
  3 pasaron. Queda pendiente reintentarla (~$0.012).

### Providers: cuál da qué
Solo **DataForSEO Business Listings** da las 7 columnas que se quieren.

| Columna | OSM | DataForSEO Listings | Scavio Maps |
|---|---|---|---|
| nombre / teléfono / tiene web | parcial | ✅ | ✅ |
| rating / reseñas | ❌ | ✅ | ✅ |
| **ficha sin reclamar** | ❌ | ✅ | ❌ |

Scavio Maps no puede dar "sin reclamar" y no es un bug: Google Maps no publica
ese dato. Alternativa viable si DataForSEO se cae: 50 créditos gratis sin
tarjeta, alcanza para Córdoba capital.

### Teléfonos argentinos y WhatsApp
Los números vienen como `+54 351 212-1912`, **sin el 9**. WhatsApp necesita
`+54 9 351 ...`. Hay que insertarlo (`wa.me/5493512121912`), **pero desde el
dato no se puede saber si es celular o línea fija** — en Córdoba ambos son
351 + 7 dígitos. Verificar que el contacto exista en WhatsApp antes de escribir.

---

## 5. Qué NO se versiona

`.gitignore` de la raíz excluye `harvey/*.csv` y `harvey/mensajes_*.md`: son
datos de comercios reales (nombre, teléfono, rating) y el repo es público.
Los scripts que los generan **sí** se versionan. `.env` y `data/` están
cubiertos por el `.gitignore` de upstream.

---

## 6. El pitch

**Producto:** sistema web de gestión para locales de celulares. Ingreso de
equipos a reparación con estados, stock, clientes, ventas, caja, y aviso
automático por WhatsApp al cliente cuando el equipo cambia de estado.

**Precios:** $60.000 ARS/mes el básico. $80.000 con WhatsApp oficial de Meta
y facturación AFIP.

**Oferta de entrada:** demo de 15 minutos sobre su propio local, sin
compromiso. O mandar un PDF corto, que tiene menos fricción en un primer
WhatsApp.

**Tono:** directo, de colega a colega, sin vender humo.

### Reglas de redacción aprendidas

- **Abrir con "tengo un local de celulares como vos".** Es la credencial más
  fuerte y lo que lo separa de cualquier vendedor de software. Nunca
  presentarse primero como desarrollador: confunde y desperdicia la carta.
- **Hablar en primera persona del singular.** "Lo hice para mi local", no
  "creamos un sistema". El plural suena a empresa.
- **Nombrar el dolor, no listar módulos.** "Saber en qué estado está cada
  equipo sin revisar el cuaderno" funciona; "reparaciones, stock, facturación
  y WhatsApp" es una lista de features que nadie pidió.
- **No poner el precio en el primer mensaje.** Mata la charla antes de que
  entiendan qué es. Tener la respuesta lista para cuando pregunten.
- **No mencionar `ficha_sin_reclamar` en el mensaje.** Sirve para rankear, no
  para abrir: suena a que investigaste al tipo y no tiene que ver con el
  producto.
- **Variar los mensajes.** Diez idénticos se detectan como spam.
- Mensajes cortos: WhatsApp, no email.

### Objeciones

| Objeción | Respuesta |
|---|---|
| "Ya uso Excel / cuaderno" | El problema no es anotar, es buscar. Te muestro una reparación de hace 3 meses en 2 segundos. |
| "Es caro" | Es menos que un cambio de pantalla por mes. |
| "No tengo tiempo de aprenderlo" | La migración y la carga inicial la hago yo. |
| "No me interesa" | Sin drama. ¿Me decís cómo lo manejan ahora? |

---

## 7. Cómo rankear los prospectos

Ordenar por **cantidad de reseñas**, no por rating. La reseña es el mejor
proxy de **volumen de reparaciones**, y el volumen es el dolor: un local con
176 reseñas tiene muchos más equipos dando vueltas que uno con 1. Rating alto
sin volumen significa que atiende poco y bien.

Filtrar a los que **reparan**, no los de puros accesorios: el sistema arranca
por el ingreso de equipos con estados.

Cruces útiles: *con teléfono Y sin web* y *con teléfono Y ficha sin reclamar*
marcan locales con volumen y poca infraestructura digital.

---

## 8. Pendientes

- Reintentar la query `accesorios para celulares` (403 en la corrida, ~$0.012).
- Las otras 3 ciudades, descomentando `icp.geography` (~$0.14).
- La columna `direccion`, si se decide parchear el upsert.
- Probar el `TAG_MAP` de OSM en una red sin bloqueo a Overpass.
- Contactar los 36 locales restantes del CSV.
