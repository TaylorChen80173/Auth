<template>
  <q-modal ref="layoutModal" :content-css="{minWidth: width, minHeight: height}">
    <q-modal-layout>
      <q-toolbar slot="header" color="darkred" :data-background-color="color">
        <div class="q-toolbar-title">
          {{title}}
        </div>
        <q-btn flat @click="$refs.layoutModal.close()">
          <q-icon name="fa-times" />
        </q-btn>
      </q-toolbar>
      <q-toolbar v-if="$slots['action']" slot="footer" color="red-10">
        <div class="q-toolbar-title">
          {{footer}}
        </div>
        <slot name="action"></slot>
        <!--<s-btn color="orange" big @click="$refs.layoutModal.close()">取消</s-btn>-->
      </q-toolbar>
      <div class="layout-padding">
        <slot name="content"></slot>
      </div>
    </q-modal-layout>
  </q-modal>
</template>

<script>
  import { QModal, QModalLayout, QBtn, QToolbar, QIcon } from 'quasar'
  export default {
    name: 's-modal',
    props: {
      color: {
        type: String,
        default: 'deepblue'
      },
      width: {
        type: String,
        default: '80vw'
      },
      height: {
        type: String,
        default: '80vh'
      },
      title: String,
      footer: String
    },
    data () {
      return {}
    },
    methods: {
      open () {
        this.$refs.layoutModal.open()
      },
      close () {
        this.$refs.layoutModal.close()
      },
      toggle () {
        this.$refs.layoutModal.toggle()
      }
    },
    components: {
      QModal, QModalLayout, QBtn, QToolbar, QIcon
    }
  }
</script>

<style>
</style>
