import { createPinia } from "pinia"
import { createApp } from "vue"
import "@/styles/global.scss"
import "@/styles/angie.css"
import App from "./App.vue"
import router from "./router"

const storeManager = createPinia()

createApp(App).use(router).use(storeManager).mount("#app")
