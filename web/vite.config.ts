import path from "path"
import vue from "@vitejs/plugin-vue"
import vuetify from "@vuetify/vite-plugin"
import { defineConfig } from "vite"
import Pages from "vite-plugin-pages"

// https://vitejs.dev/config/
export default defineConfig({
  server: {
    port: 3000, // Required for vite to be accessible when running as part of docker compose setup
  },
  plugins: [
    vue(),
    // https://github.com/vuetifyjs/vuetify-loader/tree/next/packages/vite-plugin
    vuetify({
      autoImport: true,
    }),
    Pages({
      dirs: "src/routes",
    }),
  ],
  test: {
    setupFiles: ["vuetify.config.ts", "test/setup.ts"],
    deps: {
      inline: ["vuetify"],
    },
    globals: true,
    environment: "jsdom",
    include: ["test/**/*.ts"],
    exclude: [
      "test/e2e/**/*.ts",
      "test/a11y/**/*.ts",
      "test/test-helper/**/*.ts",
      "test/setup.ts",
    ],
    coverage: {
      reporter: ["lcov"],
    },
  },
  define: {
    "process.env": {},
  },
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "src"),
      "~": path.resolve(__dirname, "test"),
    },
  },
})
