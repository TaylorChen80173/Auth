import Vuex from 'vuex'
import Vue from 'vue'
import jwtDecode from 'jwt-decode'

Vue.use(Vuex)

let state = {
  isRootScreen: true,
  layoutNeeded: true,
  loginFirst: false,
  mobileMode: false,
  menuCollapse: true,

  profileInfo: {},
  authToken: '',
  tokenInfo: {},
  accountInfo: {},
  menuLinks: []
}

let mutations = {
  setLayoutNeeded (state, value) {
    state.layoutNeeded = value
  },
  setLoginFirst (state, value) {
    state.loginFirst = value
  },
  setMobileMode (state, value) {
    state.mobileMode = value
  },
  setMenuCollapse (state, value) {
    state.menuCollapse = value
  },
  setAuthToken (state, value) {
    state.authToken = value
    state.tokenInfo = value ? jwtDecode(value) : null
  },
  setMenuLinks (state, value) {
    state.menuLinks = value
    validateAuthInfo()
  },
  setProfileInfo (state, value) {
    state.profileInfo = value
  },
  setAccountInfo (state, value) {
    state.accountInfo = value
  },
  setLogout (state) {
    state.accountInfo = {}
    state.tokenInfo = {}
    state.authToken = ''
  }
}

let getters = {
  isRootScreen () {
    return state.isRootScreen
  },
  getLayoutNeeded () {
    return state.layoutNeeded
  },
  isLoginFirst () {
    return state.isLoginFirst
  },
  getMobileMode () {
    return state.mobileMode
  },
  getMenuCollapse () {
    return state.menuCollapse
  },
  isLogin () {
    return state.accountInfo != null && (typeof state.accountInfo === 'object') && (typeof state.accountInfo.userId !== 'undefined')
  },
  tokenInfo () {
    return state.tokenInfo
  },
  authToken () {
    return state.authToken
  },
  accountInfo () {
    return state.accountInfo
  },
  getMenuLinks () {
    return state.menuLinks
  },
  profileInfo () {
    return state.profileInfo
  },
  isProduction () {
    return state.profileInfo != null && (typeof state.profileInfo === 'object') && (typeof state.profileInfo.activeProfiles !== 'undefined') &&
      state.profileInfo.activeProfiles.indexOf('prod') !== -1
  },
  swaggerEnabled () {
    return state.profileInfo != null && (typeof state.profileInfo === 'object') && (typeof state.profileInfo.activeProfiles !== 'undefined') &&
      state.profileInfo.activeProfiles.indexOf('swagger') !== -1
  },
  ribbonEnv () {
    return (state.profileInfo != null && (typeof state.profileInfo === 'object'))
      ? state.profileInfo.ribbonEnv : null
  }
}

function validateAuthInfo () {
  // let user = state.userInfo
  // let userRegionType = getters.isLogin() ? user.RegionType : ''
  // let userVolunteerType = getters.isLogin() ? user.VolunteerType : ''
  state.menuLinks.forEach(link => {
    link.routes.forEach(x => {
      // let regionType = typeof x.regionType === 'undefined' ? null : x.regionType
      // let volunteerType = typeof x.volunteerType === 'undefined' ? null : x.volunteerType
      // if ((!regionType || regionType.includes(userRegionType)) &&
        // (!volunteerType || volunteerType.includes(userVolunteerType))) {
      x.enable = true
      // }
      // else {
      //   x.enable = false
      // }
      if (x.routes) {
        x.routes.forEach(x1 => {
          x1.enable = true
        })
      }
    })
    if (link.routes.filter(x => { return x.enable }).length > 0) {
      link.enable = true
    }
    else {
      link.enable = false
    }
  })
}

export default new Vuex.Store({
  strict: true,
  state,
  mutations,
  getters
})
