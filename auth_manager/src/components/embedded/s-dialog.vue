<template>
  <q-modal class="dialog" ref="layoutModal" :content-css="{minWidth: width, minHeight: height}" minimized>
    <q-modal-layout>
      <div class="full-width row justify-center">
        <div class="row items-center justify-center mark" :style="{'backgroundColor': color}">
          <q-icon class="col" :name="icon" color="white" style="font-size: 40px"></q-icon>
        </div>
        <div class="col-12 row items-center" style="padding-top: 10px">
          <div class="col text-center text-bold" style="font-size: 20px; padding-top: 20px" v-html="title"></div>
          <q-scroll-area class="col-12" :style="{'width': width, 'height': (parseInt(height.toLowerCase().replace('px','')) - 140) + 'px'}" style="padding-top: 10px">
            <div class="items-center justify-center">
              <div class="text-center" style="font-size: 18px; padding: 0 5px" v-html="message"></div>
            </div>
          </q-scroll-area>
        </div>
      </div>
      <q-toolbar slot="footer" color="white" class="footer">
        <div class="full-width row justify-round">
          <s-btn class="col text-bold" @click="doSubmit()" big rounded :color="btnColor ? btnColor : 'green'" v-show="submitText">{{submitText}}</s-btn>
          <div v-show="submitText && cancelText" style="width: 5px"></div>
          <s-btn class="col text-bold" @click="close()" big rounded :color="btnColor ? btnColor : 'red'" v-show="cancelText">{{cancelText}}</s-btn>
        </div>
      </q-toolbar>
    </q-modal-layout>
  </q-modal>
</template>

<script>
  import { QModal, QModalLayout, QToolbar, QIcon, QScrollArea } from 'quasar'

  export default {
    name: 'SDialog',
    props: {
      height: {
        type: String,
        default: '200px'
      },
      width: {
        type: String,
        default: '300px'
      },
      color: {
        type: String,
        default: 'deepblue'
      },
      icon: String,
      title: String,
      message: String,
      submitText: String,
      cancelText: String,
      btnColor: {
        type: String,
        default: 'deepblue'
      },
      submit: Function
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
      },
      doSubmit () {
        this.submit()
        this.$refs.layoutModal.close()
      }
    },
    components: {
      QModal, QModalLayout, QToolbar, QIcon, QScrollArea
    }
  }
</script>

<style>
  .modal-title {
    font-size: 20px;
    background-color: #7b2322;
    height: 50px;
    box-shadow: 0 1px 10px rgba(0, 0, 0, 0.46);
  }
  .dialog .modal-header {
    padding: 0;
  }
  .dialog .layout-footer {
    box-shadow: none;
  }
  .dialog .q-btn-big {
    font-size: 20px;
    box-shadow: none;
    border-radius: 8px;
    height: 30px;
  }
  .dialog .modal-content {
    border-radius: 8px;
    background-color: transparent;
  }
  .dialog .q-modal-layout-content {
    width: 100%;
    margin: 25px 0 0 0;
    border-radius: 8px 8px 0 0;
    -webkit-border-radius: 8px 8px 0 0;
    background: #fff;
  }
  .dialog .footer {
    padding: 8px 8px;
    min-height: 40px;
  }
  .dialog .mark {
    position: absolute;
    display: block;
    width: 50px;
    height: 50px;
    border-radius: 50%;
    -webkit-border-radius: 50%;
    border: 3px solid white;
    top: 0;
    z-index: 1000;
  }
</style>
