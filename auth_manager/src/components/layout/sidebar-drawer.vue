<template>
  <div class="sidebar" data-background-color="black">
    <div class="sidebar-wrapper">
      <q-scroll-area style="width: 100%; height: calc(100vh - 100px); padding-right: 12px"
                     :content-style="{ zIndex: 4 }"
                     :content-active-style="{ zIndex: 4 }" :thumb-style="{
                        right: '1px', borderRadius: '3px', background: 'lightblue', width: '11px', opacity: 0.6
                     }">
        <div v-for="(parent, index) in links" :key="index">
          <q-collapsible menu v-show="parent.enable" :label="parent.name" :icon="parent.icon" :opened="parent.show ? true : false"
                         collapse-icon="fa-plus" @open="menuClick(parent, true, $event)" @close="menuClick(parent, false, $event)">
            <div class="scroll">
              <template v-for="(child, index0) in parent.routes">
                <q-side-link v-if="child.route" item v-show="child.enable && parent.show" :to="child.route" highlight exact :key="index0"
                             style="padding-left: 40px">
                  <q-item-main :label="child.name" />
                </q-side-link>
                <q-collapsible v-if="child.routes" menu v-show="child.enable" :label="child.name" :opened="child.show ? true : false"
                               style="padding-left: 25px" @open="menuClick(child, true, $event)" @close="menuClick(child, false, $event)">
                  <div class="scroll">
                    <template v-for="(child1, index1) in child.routes">
                      <q-side-link item v-show="child1.enable && child.show" :to="child1.route" highlight exact :key="index1"
                                   style="padding-left: 50px">
                        <q-item-main :label="child1.name" />
                      </q-side-link>
                    </template>
                  </div>
                </q-collapsible>
              </template>
            </div>
          </q-collapsible>
        </div>
      </q-scroll-area>
    </div>
  </div>
</template>
<script type="text/javascript">
  import { QScrollArea, QListHeader, QCollapsible, QSideLink, QItemMain, QIcon, extend } from 'quasar'
  import { mapMutations, mapGetters } from 'vuex'
  import { layout, menu } from '../../configs/constants'

  export default {
    created () {
      this.setMenuLinks(this.links)
    },
    data () {
      return {
        links: this.menuProcess(menu, this.$route.path).routes
      }
    },
    computed: {
      ...mapGetters(['getLayoutNeeded', 'getMenuCollapse', 'accountInfo', 'getIsLogin', 'getMenuLinks']),
      userType () {
        var type = this.$store.state.userInfo.UserType
        return type === 'V' ? '環保志工' : (type === 'E' ? '公務帳號' : (type === 'P' ? '民間團體' : ''))
      },
      layout () {
        return layout
      },
      userInfo () {
        return this.accountInfo.userId ? '使用者名稱：' + this.accountInfo.displayName : '使用者請登入'
      }
    },
    methods: {
      ...mapMutations(['setMenuLinks']),
      menuClick (menuLink, show) {
        menuLink.show = show
      },
      menuProcess (menu, path) {
        var routes = []
        let fShow = false
        menu.forEach(m => {
          if (m.name) {
            let mo
            if (m.menu) {
              let mp = this.menuProcess(m.menu, path)
              mo = { name: m.name, icon: m.icon, show: mp.show, routes: mp.routes }
              fShow = mp.show
            }
            else {
              mo = {name: m.name, route: m.route}
              fShow = fShow || path === m.route
            }
            routes.push(mo)
          }
        })
        return {show: fShow, routes: routes}
      },
      linkProcess (links, path) {
        let fShow = false
        links.forEach(m => {
          if (m.routes) {
            let mp = this.linkProcess(m.routes, path)
            m.show = mp.show
            fShow |= mp.show
          }
          else {
            fShow = fShow || path === m.route
          }
        })
        return {show: fShow, routes: links}
      }
    },
    watch: {
      '$route.path' (val) {
        let l = extend(true, [], this.links)
        this.links = this.linkProcess(l, val).routes
      }
    },
    components: {
      QScrollArea, QListHeader, QCollapsible, QSideLink, QItemMain, QIcon
    }
  }
</script>
<style>
  .sidebar .sidebar-wrapper,
  .sidebar .q-collapsible-sub-item {
    background-color: #213649;
  }
  .sidebar .q-collapsible .q-item-label {
    font-size: 20px;
  }
  .sidebar .q-collapsible-sub-item .q-item-label {
    font-size: 18px;
  }
  .sidebar .q-collapsible-sub-item {
    padding: 0;
  }
</style>
