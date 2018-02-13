<template>
  <q-toolbar slot="footer" :color="color">
    <q-toolbar-title>
      <div class="text-center">
        <small class="text-black">Copyright © AdvancedTEK International Corp., member of AdvancedTEK Group. All rights reserved.</small>
      </div>
    </q-toolbar-title>
  </q-toolbar>
</template>

<script>
  import { QToolbar, QToolbarTitle } from 'quasar'
  import { mapGetters } from 'vuex'
  import { layout } from '../../configs/constants'

  export default {
    computed: {
      ...mapGetters(['getLayoutNeeded']),
      color () {
        return layout.footerColor
      }
    },
    components: {
      QToolbar, QToolbarTitle
    }
  }
</script>

<style scoped>
.q-toolbar {
  min-height: 30px;
}
.q-toolbar-title {
  font-size: 14px;
}
</style>
