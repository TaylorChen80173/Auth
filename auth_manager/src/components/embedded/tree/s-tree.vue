<template>
  <div class="q-tree">
    <ul>
      <s-tree-item
        v-for="item in model"
        :key="item.id || item.title"
        :top="model"
        :value="item"
        :contract-html="contractHtml"
        :expand-html="expandHtml"
        :expandable="expandable"
        @click="treeClick"
      ></s-tree-item>
    </ul>
  </div>
</template>

<script>
  import STreeItem from './tree-item'
  import { extend } from 'quasar'
  export default {
    name: 's-tree',
    components: {
      STreeItem
    },
    created () {
      this.model = extend(true, [], this.value)
    },
    props: {
      value: {
        type: Array,
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
    data () {
      return {
        model: []
      }
    },
    methods: {
      treeClick (data) {
        this.clearClicked(this.model)
        this.$set(data, 'clicked', true)
        this.$emit('click', data)
      },
      clearClicked (tree) {
        tree.forEach(t => {
          this.$set(t, 'clicked', false)
          if (t.children && t.children.length > 0) {
            this.clearClicked(t.children)
          }
        })
      },
      checked (data, key) {
        if (key) {
          let treeModel = this.model
          if (data !== null) {
            treeModel = data
          }
          treeModel.forEach(t => {
            if (t.selfId === key.toString()) {
              // console.log(t.selfId)
              this.$set(t, 'clicked', true)
              this.$emit('click', t)
            }
            if (t.children && t.children.length > 0) {
              this.checked(t.children, key)
            }
          })
        }
      },
      refresh () {
        this.model = extend(true, [], this.value)
      }
    }
    // watch: {
    //   value (val) {
    //     this.model = extend(true, [], this.value)
    //   }
    // }
  }
</script>
