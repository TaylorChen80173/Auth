<template>
  <div>
    <q-card :color="backgroundColor">
      <div class="card-header row items-center" :class="{ 's-card-icon': icon, 'collapsible': collapsible }" @click="doCollapsible">
        <q-card-title v-show="title || icon" :class="{ 'col-auto': icon }" :data-background-color="color">
          <!--q-icon v-show="icon" :name="icon" size="30px" color="white"></q-icon-->
          <span v-show="!icon && title" :class="titleAlign" :style="{ 'color': titleColor }" v-html="title"></span>
          <div v-show="!icon" slot="right"><slot name="titleRight"></slot></div>
          <div v-show="collapsible" slot="right"><span style="font-size: 20px; color: white">請點擊縮放</span></div>
        </q-card-title>
        <span v-show="title && icon" class="s-card-title col-auto" v-html="title"></span>
        <div v-show="$slots['iconRight']" class="s-card-right col text-right gutter" style="font-size: 18px"><slot name="iconRight"></slot></div>
      </div>
      <q-card-main v-if="!collapsed" style="padding-top: 10px;">
        <slot name="content"></slot>
      </q-card-main>
      <q-card-separator v-if="!collapsed" v-show="$slots['action']" />
      <q-card-actions v-if="!collapsed" v-show="$slots['action']" :align="actionAlign">
        <div class="s-card-actions" :data-background-color="color"></div>
        <slot name="action"></slot>
      </q-card-actions>
    </q-card>
  </div>
</template>

<script>
  import { QCard, QCardTitle, QCardMain, QCardActions, QCardSeparator, QIcon, QTransition } from 'quasar'

  export default {
    props: {
      color: {
        type: String,
        default: 'darkblue'
      },
      backgroundColor: String,
      icon: String,
      title: String,
      titleColor: {
        type: String,
        default: 'white'
      },
      titleAlign: {
        type: String,
        default: 'float-left'
      },
      actionAlign: {
        type: String,
        default: 'center',
        validator: v => ['start', 'center', 'end', 'around'].includes(v)
      },
      collapsible: {
        type: Boolean,
        default: false
      }
    },
    data () {
      return {
        collapsed: false
      }
    },
    methods: {
      doCollapsible () {
        if (this.collapsible) {
          this.collapsed = !this.collapsed
        }
        this.$emit('collapsible', this.collapsed)
      }
    },
    components: {
      QCard, QCardTitle, QCardMain, QCardActions, QCardSeparator, QIcon, QTransition
    }
  }
</script>

<style scoped>
  .collapsible {
    cursor: ns-resize;
  }
</style>
