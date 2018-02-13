<template>
  <div class="toolbar" v-show="header">
    <q-toolbar slot="header" :style="{ backgroundColor: color }">
      <div class="header-inner">
        <div class="bottom clearfix">
          <a class="logo" href="index.html"><img src="../../assets/image/logo.svg" height="45px"></a>
          <div class="title">權限管理系統</div>
        </div>
      </div>
      <q-btn flat @click="leftDrawer()" color="black">
        <q-icon name="menu" />
      </q-btn>
      <q-toolbar-title>
        <div class="row gutter justify-end items-center">
          <div class="userName" style="color: black">
            <span>姓名：</span>
            <span class="name">{{accountInfo.stuName}}</span>
            <span>帳號：</span>
            <span class="idCode">{{accountInfo.stuCode}}</span>
            <span>組織：</span>
            <span class="idCode">{{accountInfo.orgName}}</span>
          </div>
          <router-link class="logoutBtn" to="/Login"><s-icon name="fa-exit"/>登出</router-link>
        </div>
      </q-toolbar-title>
      <q-btn v-show="!isRootScreen" class="within-iframe-hide" flat @click="$router.go(-1)" style="margin-right: 15px">
        <q-icon name="keyboard_arrow_left" />
        回上頁
      </q-btn>
      <q-btn v-show="isLogin" right flat @click="logout" style="margin-right: 15px">
        <q-icon name="fa-sign-out" />登出
      </q-btn>
    </q-toolbar>
  </div>
</template>

<script>
  import { QToolbar, QBtn, QIcon, QToolbarTitle, LocalStorage, SessionStorage } from 'quasar'
  import { mapGetters, mapMutations } from 'vuex'
  import { layout } from '../../configs/constants'
  import { viewMixin } from '../mixins'

  export default {
    mixins: [viewMixin],
    created () {
      /*
      this.get('profileInfo', {}, response => {
        this.setProfileInfo(response)
      })
      */
    },
    data () {
      return {
        header: false,
        toggleLeft: false
      }
    },
    computed: {
      ...mapGetters(['isRootScreen', 'isLogin', 'accountInfo']),
      color () {
        return 'white'
      },
      layout () {
        return layout
      }
    },
    components: {
      QToolbar, QBtn, QIcon, QToolbarTitle
    },
    methods: {
      ...mapMutations(['setProfileInfo', 'setLogout']),
      leftDrawer () {
        this.toggleLeft = !this.toggleLeft
        return this.$parent.toggleLeft()
      },
      logout () {
        delete this.$http.api.defaults.headers.common.Authorization

        // for swagger authentication
        LocalStorage.remove('jhi-authenticationtoken')
        SessionStorage.remove('jhi-authenticationtoken')

        this.setLogout()
        this.route('Login')
      },
      toggleHeader () {
        this.header = !this.header
      },
      displayHeader (disp) {
        this.header = disp
      }
    }
  }
</script>

<style scoped>
  .header-inner {
    z-index: 100;
    background-color: white;
    height: 45px;
  }
  .header-inner .bottom {
    padding: 0px;
    width: 280px;
  }
  .header-inner .bottom .logo {
    display: inline-block;
    float: left;
  }
  .header-inner .bottom .title {
    display: inline-block;
    font-size: 25px;
    font-weight: 600;
    line-height: 45px;
    float: left;
    color: black;
    padding-left: 10px;
  }
  .logoutBtn {
    background-color: #7b2322;
    width: 100px;
    line-height: 36px;
    margin-left: 20px;
    padding: 1px 5px;
    cursor: pointer;
    vertical-align: middle;
    text-align: center;
    color: #fff;
    border-radius: 6px;
  }
  .logoutBtn:hover {
    background-color: #fff;
    color: #7b2322;
    font-weight: 700;
  }
</style>
