<template>
  <li class="q-tree-item">
    <div
      class="row inline items-center"
      :class="{'q-tree-link': value.handler || isExpandable}"
    >
      <div @click="tap" class="q-tree-label relative-position row items-center" v-ripple.mat>
        <q-icon v-if="value.icon" :name="value.icon" class="on-left"></q-icon>
        <label ref="label" v-html="value.title" :class="{ 'text-red': value.clicked, 'text-black': !value.clicked }" style="cursor: pointer"></label>
      </div>
      <span v-if="isExpandable" @click="toggle" class="on-right" v-html="value.expanded ? contractHtml : expandHtml"></span>
    </div>
    <q-slide-transition>
      <ul v-show="isExpandable && value.expanded">
        <s-tree-item v-for="item in value.children" :key="item.id || item.title" :top="top" :value="item" :contract-html="contractHtml" :expand-html="expandHtml" @click="treeClick"></s-tree-item>
      </ul>
    </q-slide-transition>
  </li>
</template>

<script>
  import { QIcon, QSlideTransition } from 'quasar'
  export default {
    name: 's-tree-item',
    components: {
      QIcon,
      QSlideTransition
    },
    props: {
      top: {
        type: Array,
        required: true
      },
      value: {
        type: Object,
        required: true
      },
      contractHtml: {
        type: String,
        default: '<i class="fa fa-minus-circle"></i>'
      },
      expandHtml: {
        type: String,
        default: '<i class="fa fa-plus-circle"></i>'
      },
      expandable: {
        type: Boolean,
        default: true
      }
    },
    methods: {
      tap () {
        this.$emit('click', this.value)
//        if (this.expandable) {
//          this.toggle()
//        }
      },
      toggle () {
        if (this.isExpandable) {
          this.value.expanded = !this.value.expanded
        }
      },
      treeClick (value) {
        this.$emit('click', value)
      }
    },
    computed: {
      isExpandable () {
        return this.value.children && this.value.children.length
      }
    }
  }
</script>
