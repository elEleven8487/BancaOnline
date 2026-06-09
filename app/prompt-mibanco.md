# Prompt — App **MiBanco** (Android Studio · Kotlin · Firebase · MVVM)

> **Cómo usar este prompt:** Pégalo completo en la herramienta de IA que vayas a usar (Claude, Claude Code, Cursor, ChatGPT, etc.) **junto con el PDF `MiBanco-Design-Doc.pdf`** para que pueda ver los mockups y constraints visuales. Si la herramienta no acepta PDF, adjunta capturas de las 11 páginas.

---

## 1. Contexto y objetivo

Necesito que generes el código **completo y compilable** de una app Android bancaria llamada **MiBanco**. Es un **proyecto escolar**, así que prioriza en este orden:

1. Que **compile y corra** sin pasos manuales extra (más allá de configurar Firebase).
2. Que **siga las convenciones modernas** de Android (View Binding, Coroutines, ViewModel, Navigation Component, MVVM).
3. **Estructura clara pero NO sobre-ingenierada.** No uses Hilt/Dagger, no uses Clean Architecture con 3 capas de mapeo, no uses módulos Gradle separados. Repositorios simples como singletons o `object`, eso basta.
4. Código **legible y comentado** en español para puntos no obvios (validaciones, flujo de Firebase, mapeos).

---

## 2. Stack técnico (OBLIGATORIO)

| Aspecto | Decisión |
|---|---|
| Lenguaje | **Kotlin** |
| Build | Gradle **KTS** (`build.gradle.kts`) |
| `minSdk` | 24 |
| `targetSdk` / `compileSdk` | 34 |
| UI | **XML** con `ConstraintLayout` / `LinearLayout` — **NO uses Jetpack Compose** |
| Binding | **View Binding** habilitado (prohibido `findViewById`) |
| Navegación | **Navigation Component** (NavGraph) para los Fragments del Bottom Nav y para el sub-flujo de transferencia. Activities separadas para Login, Registro, DatosPersonales, AddBeneficiario y la MainActivity (host del bottom nav). |
| Arquitectura | **MVVM** — `Activity/Fragment` ↔ `ViewModel` (con `StateFlow`) ↔ `Repository` ↔ Firebase |
| Asincronía | **Kotlin Coroutines** + `Flow` / `StateFlow` (usa `viewModelScope` y `lifecycleScope`) |
| Auth | **Firebase Authentication** (email + password) |
| Base de datos | **Cloud Firestore** |
| Storage | **Firebase Storage** (solo para foto de perfil) |
| Imágenes | **Glide** |
| Componentes | **Material Components 3** (`com.google.android.material`) |
| DI | Ninguno (repositorios como `object` singleton) |

---

## 3. Estructura de carpetas

Usa `com.example.mibanco` como paquete base.

```
app/src/main/
├── java/com/example/mibanco/
│   ├── MiBancoApp.kt                 # Application class
│   ├── data/
│   │   ├── model/
│   │   │   ├── User.kt
│   │   │   ├── Beneficiario.kt
│   │   │   └── Movimiento.kt
│   │   └── repository/
│   │       ├── AuthRepository.kt
│   │       ├── UserRepository.kt
│   │       ├── BeneficiarioRepository.kt
│   │       └── MovimientoRepository.kt
│   ├── ui/
│   │   ├── auth/
│   │   │   ├── LoginActivity.kt          + LoginViewModel.kt
│   │   │   ├── RegisterActivity.kt       + RegisterViewModel.kt
│   │   │   └── DatosPersonalesActivity.kt + DatosPersonalesViewModel.kt
│   │   ├── main/
│   │   │   └── MainActivity.kt           # host del BottomNav + NavHostFragment
│   │   ├── home/
│   │   │   ├── HomeFragment.kt           + HomeViewModel.kt
│   │   │   └── MovimientoAdapter.kt
│   │   ├── beneficiarios/
│   │   │   ├── BeneficiariosFragment.kt  + BeneficiariosViewModel.kt
│   │   │   ├── BeneficiarioAdapter.kt
│   │   │   └── AddBeneficiarioActivity.kt + AddBeneficiarioViewModel.kt
│   │   ├── cuenta/
│   │   │   └── CuentaFragment.kt         + CuentaViewModel.kt
│   │   └── transferir/
│   │       ├── SeleccionarBeneficiarioFragment.kt
│   │       ├── MontoTransferenciaFragment.kt
│   │       └── TransferenciaViewModel.kt # compartido vía navGraphViewModels
│   └── util/
│       ├── Extensions.kt                 # extensiones (formatCurrency, etc.)
│       └── Validators.kt                 # validaciones de email/password
├── res/
│   ├── layout/                           # uno por pantalla + items + dialogs
│   ├── menu/bottom_nav_menu.xml
│   ├── navigation/main_nav_graph.xml     # Home, Beneficiarios, Cuenta + sub-flujo Transferir
│   ├── drawable/                         # ic_arrow_back, ic_search, ic_add, ic_settings, ic_logout, ic_chevron, ic_camera, ic_check_circle, ic_more_vert, ic_arrow_down, backgrounds custom
│   ├── values/
│   │   ├── colors.xml
│   │   ├── dimens.xml
│   │   ├── strings.xml
│   │   └── themes.xml                    # Theme.MiBanco basado en Material3
│   └── font/                             # opcional: Inter o usa default
└── AndroidManifest.xml
```

---

## 4. Sistema de diseño

> Mira el PDF, página **2 — Sistema de diseño**, para referencia visual.

### `colors.xml`
```xml
<color name="primary_dark">#1E40AF</color>
<color name="primary">#2563EB</color>
<color name="primary_light">#3B82F6</color>
<color name="success">#10B981</color>
<color name="text_primary">#0F172A</color>
<color name="text_secondary">#64748B</color>
<color name="background">#F8FAFC</color>
<color name="border">#E2E8F0</color>
<color name="danger_bg">#FEF2F2</color>
<color name="danger_stroke">#FECACA</color>
<color name="danger_text">#DC2626</color>
<color name="search_bg">#F1F5F9</color>
```

### `dimens.xml` (8-pt grid)
```
space_xxs = 4dp · space_xs = 8dp · space_s = 12dp
space_m = 16dp · space_l = 24dp · space_xl = 32dp · space_xxl = 48dp
input_height = 56dp · button_height = 56dp
corner_radius_input = 12dp · corner_radius_card = 18dp
```

### Tipografía
| Estilo | Tamaño | Peso | Uso |
|---|---|---|---|
| Display | 24sp | bold | títulos de pantalla |
| Title | 18sp | semibold | subtítulos / saldo |
| Body | 14sp | regular | texto general |
| Caption | 12sp | regular | labels / helpers |
| Micro | 10sp | medium | tabs / estados |

### Componentes base
- `TextInputLayout` → alto 56dp, radius 12dp, borde 1.5dp, hint flotante 12sp
- `MaterialButton primary` → alto 56dp, radius 12dp, texto blanco semibold
- `MaterialButton outlined` → borde primary, texto primary
- `TextButton` → enlaces y acciones secundarias

---

## 5. Modelo de datos (Firestore)

### Colección `users/{uid}`
```kotlin
data class User(
    val uid: String = "",
    val email: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val celular: String = "",
    val fechaNacimiento: Long = 0L,        // timestamp en millis
    val fotoUrl: String? = null,
    val saldo: Double = 10000.0,           // saldo demo inicial
    val numeroCuenta: String = ""          // últimos 4 dígitos visibles (ej. "8421")
)
```

### Subcolección `users/{uid}/beneficiarios/{id}`
```kotlin
data class Beneficiario(
    val id: String = "",
    val nombre: String = "",
    val banco: String = "",                // ej. "BBVA", "Banorte", "Santander"
    val cuenta: String = "",               // últimos 4 dígitos (ej. "4521")
    val creadoEn: Long = 0L
)
```

### Subcolección `users/{uid}/movimientos/{id}`
```kotlin
data class Movimiento(
    val id: String = "",
    val tipo: String = "TRANSFERENCIA",    // "TRANSFERENCIA" | "FONDEO"
    val monto: Double = 0.0,
    val descripcion: String = "",
    val beneficiarioNombre: String? = null,
    val fecha: Long = 0L
)
```

> Usa subcolecciones (no campos array) para beneficiarios y movimientos. Cada `Repository` debe exponer `Flow<List<T>>` usando `snapshotListener` envuelto en `callbackFlow`.

### Reglas de seguridad sugeridas
```
rules_version = '2';
service cloud.firestore {
  match /databases/{db}/documents {
    match /users/{uid} {
      allow read, write: if request.auth != null && request.auth.uid == uid;
      match /{sub=**} {
        allow read, write: if request.auth != null && request.auth.uid == uid;
      }
    }
  }
}
```

---

## 6. Pantallas (resumen — detalles visuales en el PDF)

> Para cada pantalla, el PDF tiene **IDs sugeridos (en azul)** y **constraints en dp (en rojo)**. Respétalos.

| # | Pantalla | Tipo | IDs clave |
|---|---|---|---|
| 1 | **Login** (`activity_login.xml`) | Activity | `imgLogo`, `etEmail`, `etPassword`, `tvForgotPassword`, `btnLogin`, `tvGoToRegister` |
| 2 | **Registro** (`activity_register.xml`) | Activity | `btnBack`, `etEmail`, `etPassword`, `etConfirmPassword`, `btnRegister`, `tvGoToLogin` |
| 3 | **Datos personales** (`activity_datos_personales.xml`) | Activity | `progressIndicator`, `etNombre`, `etApellidos`, `etCelular`, `etFechaNacimiento` (abre `MaterialDatePicker`), `btnContinuar` (anclado abajo) |
| 4 | **Home / Dashboard** (`fragment_home.xml`) | Fragment | `imgAvatar`, `cardSaldo`, `btnFondear`, `btnTransferir`, `rvMovimientos`, `layoutEmptyMovimientos`, `bottomNav` |
| 5 | **Beneficiarios** (`fragment_beneficiarios.xml`) | Fragment | `tvTitle`, `btnAddBeneficiario`, `etSearch`, `rvBeneficiarios`, `layoutEmptyBeneficiarios`, `btnAgregarPrimero` |
| 6 | **Cuenta** (`fragment_cuenta.xml`) | Fragment | `imgAvatar`, `btnEditPhoto`, `tvUserName`, `tvUserEmail`, `cardPersonalInfo`, `btnConfiguracion`, `btnCerrarSesion`, `tvVersion` |
| 7 | **Transferir · Seleccionar beneficiario** (`fragment_seleccionar_beneficiario.xml`) | Fragment (NavGraph transferir) | `progressIndicator` (50%), `etSearch`, `cardAddNuevo`, `rvBeneficiariosSelector` |
| 8 | **Transferir · Monto y concepto** (`fragment_monto_transferencia.xml`) | Fragment (NavGraph transferir) | `cardBeneficiarioSeleccionado`, `btnCambiar`, `etMonto`, `tvSaldoDisponible` (chip), `chipGroupSugeridos`, `etConcepto`, `btnTransferir` |

### Items reutilizables
- `item_beneficiario.xml` — 64dp alto, avatar circular 40dp, nombre 14sp bold, banco 11sp secondary, `btnMore` solo en modo CRUD; en modo "selector" se oculta el `btnMore` y todo el item es clickable. Pasa un parámetro `mode: SELECT | CRUD` al adapter.
- `item_movimiento.xml` — 64dp alto, ícono 40dp + descripción + monto.

---

## 7. Flujo de navegación

> Mira el PDF página **3 — Flujo de navegación**.

```
LOGIN ─acceso correcto──▶ HOME ──tab──▶ BENEFICIARIOS
  │  ◀──volver───┐                └─tab──▶ CUENTA
  │              │                
  └─Registrarse─▶ REGISTRO ─continuar─▶ DATOS PERSONALES ─finalizar (clear back stack)──▶ HOME
                                                                                            │
                                                  ┌──btnTransferir (NavGraph "transferir")──┘
                                                  ▼
                                      SELECCIONAR BENEFICIARIO ─tap item─▶ MONTO Y CONCEPTO
                                                  │                              │
                                                  └◀── btnBack ──┘   btnTransferir ─éxito──▶ popBackStack a HOME + Snackbar
```

### Notas
- `MainActivity` aloja un `FragmentContainerView` con `app:defaultNavHost="true"` y un `BottomNavigationView` con 3 items: Inicio, Beneficiarios, Cuenta.
- Login/Registro/DatosPersonales son **Activities separadas** (no fragments del nav host principal). Al completar DatosPersonales, lanza `MainActivity` con `FLAG_ACTIVITY_CLEAR_TASK | FLAG_ACTIVITY_NEW_TASK`.
- El sub-flujo de transferencia es un **nested nav graph** (`transferir_graph.xml`) que arranca desde `btnTransferir` del Home y al terminar hace `popBackStack(R.id.homeFragment, false)`.
- Usa `navGraphViewModels(R.id.transferir_graph)` para que `SeleccionarBeneficiarioFragment` y `MontoTransferenciaFragment` compartan el mismo `TransferenciaViewModel`.

---

## 8. Validaciones (usa `util/Validators.kt`)

- **Email:** `Patterns.EMAIL_ADDRESS.matcher(...).matches()`
- **Password:** mínimo 8 caracteres
- **Confirmar password:** debe coincidir
- **Celular:** mínimo 10 dígitos
- **Fecha de nacimiento:** mayor de 18 años
- **Monto de transferencia:** `> 0` y `<= saldo`. Si excede, el chip `tvSaldoDisponible` cambia a fondo `danger_bg`, texto `danger_text`, ícono ⚠, y `btnTransferir.isEnabled = false`.

Muestra errores con `textInputLayout.error = "..."` y limpia con `error = null` al editar.

---

## 9. Lógica clave a implementar

### `AuthRepository`
- `suspend fun login(email, password): Result<FirebaseUser>`
- `suspend fun register(email, password): Result<FirebaseUser>`
- `fun currentUser(): FirebaseUser?`
- `fun logout()`

### `UserRepository`
- `suspend fun createUserProfile(user: User): Result<Unit>` — escribe en `users/{uid}`. Genera `numeroCuenta` con 4 dígitos aleatorios.
- `fun observeUser(uid: String): Flow<User?>` — `snapshotListener` envuelto en `callbackFlow`.
- `suspend fun updateFotoPerfil(uid: String, localUri: Uri): Result<String>` — sube a Storage en `avatars/{uid}.jpg`, devuelve la URL pública y actualiza `fotoUrl` en Firestore.

### `BeneficiarioRepository`
- `fun observe(uid: String): Flow<List<Beneficiario>>`
- `suspend fun add(uid: String, b: Beneficiario): Result<Unit>`
- `suspend fun update(uid: String, b: Beneficiario): Result<Unit>`
- `suspend fun delete(uid: String, id: String): Result<Unit>`

### `MovimientoRepository`
- `fun observe(uid: String, limit: Int = 20): Flow<List<Movimiento>>` — ordenado por `fecha DESC`
- `suspend fun registrarTransferencia(uid: String, beneficiario: Beneficiario, monto: Double, concepto: String?): Result<Unit>`
  - **Debe hacerse en un `runTransaction` de Firestore**: lee `saldo`, valida que sea suficiente, descuenta, escribe `movimientos/{id}` con `tipo = TRANSFERENCIA`.

### `HomeViewModel`
Expone:
```kotlin
data class HomeUiState(
    val nombreUsuario: String = "",
    val saldo: Double = 0.0,
    val numeroCuenta: String = "",
    val movimientos: List<Movimiento> = emptyList(),
    val isLoading: Boolean = false
)
val uiState: StateFlow<HomeUiState>
```
Combina `userRepository.observeUser(uid)` y `movimientoRepository.observe(uid)` con `combine`.

### `TransferenciaViewModel` (compartido)
Expone:
```kotlin
data class TransferenciaUiState(
    val beneficiarioSeleccionado: Beneficiario? = null,
    val monto: Double = 0.0,
    val concepto: String = "",
    val saldoDisponible: Double = 0.0,
    val montoValido: Boolean = false,
    val mensajeError: String? = null,
    val transferenciaExitosa: Boolean = false
)
```
Funciones: `seleccionarBeneficiario(b)`, `actualizarMonto(s)`, `actualizarConcepto(s)`, `ejecutarTransferencia()`.

### Foto de perfil (`CuentaFragment` → `btnEditPhoto`)
- Abre un `BottomSheetDialog` con opciones **Cámara / Galería**.
- Pide permisos `READ_MEDIA_IMAGES` (API 33+) o `READ_EXTERNAL_STORAGE` (<33) y `CAMERA`.
- Usa `ActivityResultContracts.PickVisualMedia` para galería y `TakePicture` para cámara.
- Sube a Storage con `UserRepository.updateFotoPerfil(...)` y refresca la imagen con Glide.

---

## 10. Convenciones de UI

- **Toolbars sin sombra**, fondo `background`, título centrado 16sp bold.
- **Status bar** color `primary_dark` en Login/Registro/DatosPersonales; transparente con fondo `background` en el resto.
- Todas las pantallas con inputs deben ir envueltas en `ScrollView` para que el teclado no tape los campos.
- Botón principal **anclado al `bottom→parent`** en pantallas tipo formulario (Datos personales, Monto transferencia).
- `RecyclerView` siempre con `LinearLayoutManager(VERTICAL)` y `clipToPadding="false"`.
- Estado vacío (movimientos / beneficiarios): `LinearLayout` centrado con ícono + título + descripción + (opcional) botón CTA. Se conmuta `visibility` con el RecyclerView desde el ViewModel.

---

## 11. Build files

### `build.gradle.kts` (project)
Plugins: `com.android.application 8.x`, `org.jetbrains.kotlin.android 1.9.x`, `com.google.gms.google-services`.

### `build.gradle.kts` (app) — dependencias mínimas
```kotlin
// AndroidX core
implementation("androidx.core:core-ktx:1.13.1")
implementation("androidx.appcompat:appcompat:1.7.0")
implementation("com.google.android.material:material:1.12.0")
implementation("androidx.constraintlayout:constraintlayout:2.1.4")

// Lifecycle + ViewModel + Coroutines
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

// Fragment + Navigation
implementation("androidx.fragment:fragment-ktx:1.8.2")
implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

// Firebase (BoM)
implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-storage-ktx")

// Glide
implementation("com.github.bumptech.glide:glide:4.16.0")

// Activity / ViewBinding
implementation("androidx.activity:activity-ktx:1.9.1")
```

Habilita:
```kotlin
buildFeatures { viewBinding = true }
```

### `AndroidManifest.xml`
- Permisos: `INTERNET`, `CAMERA`, `READ_MEDIA_IMAGES` (API 33+), `READ_EXTERNAL_STORAGE` (`maxSdkVersion="32"`).
- `application.name=".MiBancoApp"`.
- Declara **LoginActivity** como launcher.
- Declara `RegisterActivity`, `DatosPersonalesActivity`, `MainActivity`, `AddBeneficiarioActivity`.

---

## 12. Qué quiero que generes (entregables)

Por favor genera el código **en este orden**, archivo por archivo, con su ruta completa al inicio de cada bloque:

1. `build.gradle.kts` (project y app) + `settings.gradle.kts`
2. `AndroidManifest.xml`
3. `MiBancoApp.kt`
4. `data/model/*.kt` (User, Beneficiario, Movimiento)
5. `data/repository/*.kt` (Auth, User, Beneficiario, Movimiento)
6. `util/Validators.kt` y `util/Extensions.kt`
7. `res/values/colors.xml`, `dimens.xml`, `strings.xml`, `themes.xml`
8. `res/drawable/*` — drawables custom necesarios (backgrounds redondeados de la card de saldo, del search, del chip de saldo, etc.). Para íconos del sistema usa los de Material (`@drawable/ic_arrow_back_24`, etc.) o indica en un comentario que se importen desde *Vector Asset Studio*.
9. **Por cada pantalla, en este orden:** layout XML → ViewModel → Activity/Fragment → Adapter (si aplica).
   - Login → Registro → Datos personales → Main + bottom nav menu → Home → Beneficiarios + AddBeneficiario → Cuenta → SeleccionarBeneficiario → MontoTransferencia
10. `res/navigation/main_nav_graph.xml` (con el nested graph de transferir)
11. `res/menu/bottom_nav_menu.xml`
12. Al final: un bloque con las **reglas de Firestore** y un **README corto** explicando:
    - Cómo crear el proyecto en Firebase Console
    - Dónde colocar `google-services.json`
    - Cómo habilitar Email/Password en Auth
    - Cómo publicar las reglas de Firestore

---

## 13. Restricciones / errores comunes a evitar

- ❌ **NO** uses `findViewById`. Todo con View Binding.
- ❌ **NO** uses `GlobalScope`. Solo `viewModelScope` y `lifecycleScope`.
- ❌ **NO** dejes `onCreateView` con código de lógica de negocio — toda lógica en el ViewModel.
- ❌ **NO** uses callbacks anidados de Firebase. Convierte las `Task` a `suspend` con `.await()` (`kotlinx-coroutines-play-services`) o envuelve listeners en `callbackFlow`.
- ❌ **NO** hardcodees strings de UI en los archivos Kotlin — usa `strings.xml`.
- ❌ **NO** olvides el `binding = null` en `Fragment.onDestroyView()`.
- ✅ Sí maneja errores: bloque `Result<T>` o `try/catch` en repositorios, y muestra `Snackbar` o `setError` en la UI.
- ✅ Sí pon comentarios `// TODO` en las partes "fuera de alcance" (ForgotPassword, Configuracion, Fondear) para que sean evidentes.

---

## 14. Criterio de "terminado"

Considero el proyecto entregado cuando:

- [ ] Puedo abrir el zip/repo en Android Studio, hacer *Sync* y compilar sin errores.
- [ ] Puedo registrarme con email/password, completar Datos personales, llegar al Home y ver mi nombre + saldo $10,000.
- [ ] Puedo agregar un beneficiario, verlo en la lista, editarlo y eliminarlo.
- [ ] Puedo hacer una transferencia: seleccionar beneficiario, capturar monto, validar contra saldo, confirmar, y al volver al Home veo el saldo descontado y el movimiento en la lista.
- [ ] Puedo cerrar sesión y volver al Login.
- [ ] Puedo cambiar mi foto de perfil desde la pantalla Cuenta.

---

**Empieza ya.** Genera todo el código en el orden indicado en la sección 12. Si algo del PDF no queda claro, asume la decisión más alineada con Material Design 3 y déjalo comentado con `// NOTA:`.
