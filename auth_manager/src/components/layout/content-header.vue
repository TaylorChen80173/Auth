<template>
  <div class="content-header" v-if="!isLoginFirst">
    <!--span id="content-header-title" class="vertical-bottom">{{routerTitle}}</span>
    <button class="bg-blue-grey-5 text-white " id="switchLayout" @click="setLayoutNeeded(!getLayoutNeeded)"><i>settings</i></button>
    <button class="bg-blue-grey-5 text-white " v-show="isLogin" id="logout" @click="logout"><i>exit_to_app</i></button>
    <hr-->
  </div>
</template>
<script type="text/javascript">
import { mapMutations, mapGetters } from 'vuex'
export default {
  computed: {
    ...mapGetters(['getLayoutNeeded', 'isLoginFirst', 'getMenuCollapse', 'isLogin', 'getMenuLinks']),
    routerTitle () {
      return this.$route.meta.name
    }
  },
  methods: {
    ...mapMutations(['setLayoutNeeded', 'setMenuCollapse', 'setAuthToken', 'setMenuLinks']),
    logout () {
      this.setAuthToken(null)
      if (!this.isLogin) {
        this.$http.api.defaults.headers.common['Authorization'] = ''
      }
      this.setMenuLinks(this.getMenuLinks)
      this.$events.$emit('loginChange', this.getMenuLinks)
      this.$router.push('/')
    }
  }
}
</script>
<style scoped>
.list {
  min-height: 120px;
}

#switchLayout {
  position: absolute;
  right: 65px;
  top: 7px;
}

#logout {
  position: absolute;
  right: 125px;
  top: 7px;
}

.content-header {
  position: relative;
  padding: 1em 4em 0em 4em;
}

@media screen and (max-width: 600px) {
  .content-header {
    padding: 1.5em .5em;
  }
  #switchLayout
  {
    position: absolute;
    right: 8px;
    top: 12px;
  }
}

#content-header-title {
  font-size: 22px;
  font-weight: bold;
}
</style>
