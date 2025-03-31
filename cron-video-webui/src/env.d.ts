/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_BASE_API: string
  readonly VITE_ARIA2_WS_URL: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
} 