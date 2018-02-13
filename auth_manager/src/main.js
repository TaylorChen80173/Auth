// === DEFAULT / CUSTOM STYLE ===
// WARNING! always comment out ONE of the two require() calls below.
// 1. use next line to activate CUSTOM STYLE (./src/themes)
// require(`./themes/app.${__THEME}.styl`)
// 2. or, use next line to activate DEFAULT QUASAR STYLE
require(`quasar/dist/quasar.${__THEME}.css`)
// ==============================

// Uncomment the following lines if you need IE11/Edge support
// require('vue-beauty/package/style/vue-beauty.min.css')
require(`quasar/dist/quasar.ie`)
require(`quasar/dist/quasar.ie.${__THEME}.css`)
require('assets/css/global.css')
// require('assets/css/all.css')

import Vue from 'vue'
import Quasar from 'quasar'
import Vuetify from 'vuetify'
import Vuelidate from 'vuelidate'

import axios from './configs/axios'
import router from './configs/router'
import store from './configs/store'

import Embedded from './components/embedded'

Vue.config.productionTip = false
Vue.use(Quasar) // Install Quasar Framework
Vue.use(Vuetify)
Vue.use(Vuelidate)
Vue.use(axios)
Vue.use(Embedded)

router.beforeEach((to, from, next) => {
  // if (!store.state.loginFirst && to.path !== '/') {
  //   next('/')
  // }
  // else {
  next()
  // }
})

if (__THEME === 'mat') {
  require('quasar-extras/roboto-font')
}
import 'quasar-extras/material-icons'
// import 'quasar-extras/ionicons'
import 'quasar-extras/fontawesome'
import 'quasar-extras/animate'
// import 'quasar-extras/animate/fadeIn.css'
// import 'quasar-extras/animate/fadeOut.css'

Quasar.start(() => {
  /* eslint-disable no-new */
  new Vue({
    el: '#q-app',
    router,
    store,
    render: h => h(require('./App'))
  })
})
