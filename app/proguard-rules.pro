# ── Kotlin Serialization ─────────────────────────────────────────────────────
# Navigation route objects in Screen.kt are annotated with @Serializable.
# The Navigation runtime encodes/decodes them via the compiler-generated
# $serializer; keep the entire navigation package so R8 does not rename it.
-keepattributes *Annotation*, InnerClasses
-keep class commanderpepper.labophase.navigation.** { *; }

# ── Room ──────────────────────────────────────────────────────────────────────
# Room.databaseBuilder looks up the KSP-generated *_Impl class by name at
# runtime — prevent R8 from renaming or stripping it.
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *

# ── Koin ──────────────────────────────────────────────────────────────────────
# Koin 4.x resolves dependencies through reified inline functions whose type
# references are baked in at compile time — no extra keep rules needed.

# ── AboutLibraries ────────────────────────────────────────────────────────────
# Prevent R8 resource shrinking from stripping the generated license JSON file.
-keep class com.mikepenz.aboutlibraries.** { *; }
