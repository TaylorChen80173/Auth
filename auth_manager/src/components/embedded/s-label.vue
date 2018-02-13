<template>
  <div class="row items-center" :class="{ 'justify-start': isActive, 'justify-end': !isActive }" :style="style">
    <s-icon v-if="flag" name="fa-star" color="red" class="col-auto" ></s-icon>
    <span class="col-auto text-bold" style="text-align: right; padding-right: 8px"><slot></slot></span>
  </div>
</template>
<script>
  export default {
    name: 'SLabel',
    props: {
      top: String,
      left: String,
      bottom: String,
      right: String,
      isActive: {
        type: Boolean,
        default: false
      },
      flag: {
        type: Boolean,
        default: false
      },
      sflag: {
        type: Boolean,
        default: false
      }
    },
    computed: {
      style () {
        let s = {}
        if (this.top) {
          s.paddingTop = this.top + 'px'
        }
        if (this.left) {
          s.paddingLeft = this.left + 'px'
        }
        if (this.bottom) {
          s.paddingBottom = this.bottom + 'px'
        }
        if (this.right) {
          s.paddingRight = this.right + 'px'
        }
        return s
      }
    }
  }
</script>
<style></style>
