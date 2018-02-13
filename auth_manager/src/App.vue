<template>
  <div id="q-app">
    <q-layout ref="layout" v-model="sides"
              :view="layout.view"
              :left-breakpoint="layout.leftBreakpoint"
              :right-breakpoint="layout.rightBreakpoint"
              :reveal="layout.reveal">
      <q-ajax-bar color="#80cbc4"></q-ajax-bar>
      <toolbar-header slot="header" ref="header"></toolbar-header>
      <sidebar-drawer slot="left" ref="sidebar"></sidebar-drawer>
      <div class="layout-view">
        <content-header></content-header>
        <transition name="fade" mode="out-in">
          <router-view></router-view>
        </transition>
      </div>
      <toolbar-footer slot="footer"></toolbar-footer>
    </q-layout>
    <s-dialog ref="modal"
              :title="dialogData.title"
              :message="dialogData.message"
              :color="dialogData.color"
              :icon="dialogData.icon"
              :submit-text="dialogData.submitText"
              :cancel-text="dialogData.cancelText"
              :submit="dialogData.submit"
              :btn-color="dialogData.btnColor">
    </s-dialog>
  </div>
</template>

<script>
  import { extend, QLayout, QAjaxBar, QScrollArea, QListHeader, QCollapsible, QSideLink, QItemMain } from 'quasar'
  import { layout } from './configs/constants'
  import toolbarHeader from './components/layout/toolbar-header.vue'
  import sidebarDrawer from './components/layout/sidebar-drawer.vue'
  import contentHeader from './components/layout/content-header.vue'
  import toolbarFooter from './components/layout/toolbar-footer.vue'
  import { mapGetters } from 'vuex'

  export default {
    name: 'app',
    provide () {
      return {
        app: this
      }
    },
    mounted () {
      this.$http.app(this)
    },
    data () {
      return {
        sides: {
          left: false,
          right: false
        },
        dialogData: {
          title: '',
          message: '',
          icon: '',
          color: '',
          submitText: '',
          cancelText: '',
          btnColor: '',
          submit: () => {}
        },
        layout
      }
    },
    computed: {
      ...mapGetters(['getLayoutNeeded', 'getMobileMode'])
    },
    components: {
      QLayout,
      QAjaxBar,
      QScrollArea,
      QListHeader,
      QCollapsible,
      QSideLink,
      QItemMain,
      toolbarHeader,
      sidebarDrawer,
      contentHeader,
      toolbarFooter
    },
    methods: {
      toggleLeft () {
        this.$refs.layout.toggleLeft()
      },
      toggleFull () {
        this.$refs.header.toggleHeader()
        this.sides.left = !this.sides.left
      },
      expand () {
        this.$refs.header.displayHeader(true)
        this.sides.left = true
      },
      shrink () {
        this.$refs.header.displayHeader(false)
        this.sides.left = false
      },
      openDialog (data) {
        this.dialogData = extend(false, {
          title: '',
          message: '',
          icon: '',
          color: '',
          submitText: '',
          cancelText: '',
          btnColor: '',
          submit: () => { this.$refs.modal.close() }
        }, data)
        this.$refs.modal.open()
      },
      closeDialog () {
        this.$refs.modal.close()
      }
    }
  }
</script>

<style></style>
