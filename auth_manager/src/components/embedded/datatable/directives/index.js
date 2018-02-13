import Drag from './v-drag'

let Directives = {
  install (Vue, options) {
    Vue.directive('drag', Drag)
  }
}

export {
  Drag
}

export default Directives
