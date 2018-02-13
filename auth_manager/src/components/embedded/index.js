import { QTransition, QBtn, QIcon } from 'quasar'
import sTree from './tree/s-tree'
import sField from './s-field'
import sCard from './s-card'
import sInput from './s-input'
import sSelect from './s-select'
import sCheckbox from './s-checkbox'
import sCheckboxs from './s-checkboxs'
import sRadio from './s-radio'
import sRadios from './s-radios'
import sDatetime from './s-datetime'
import sModal from './s-modal'
import sUploader from './uploader/s-uploader'
import sLabel from './s-label'
import sDialog from './s-dialog'

import paginator from './datatable/paginator'
import datatable from './datatable/datatable'
import datatableColumn from './datatable/datatable-column'
import drag from './datatable/directives'

let Embedded = {
  install (Vue, options) {
    Vue.component('s-transition', QTransition)
    Vue.component('s-btn', QBtn)
    Vue.component('s-icon', QIcon)
    Vue.component('s-card', sCard)
    Vue.component('s-tree', sTree)
    Vue.component('s-field', sField)
    Vue.component('s-input', sInput)
    Vue.component('s-select', sSelect)
    Vue.component('s-checkbox', sCheckbox)
    Vue.component('s-checkboxs', sCheckboxs)
    Vue.component('s-radio', sRadio)
    Vue.component('s-radios', sRadios)
    Vue.component('s-datetime', sDatetime)
    Vue.component('s-uploader', sUploader)
    Vue.component('s-modal', sModal)
    Vue.component('s-label', sLabel)
    Vue.component('s-dialog', sDialog)

    Vue.use(drag)
    Vue.component('s-paginator', paginator)
    Vue.component('s-datatable', datatable)
    Vue.component('s-datatable-column', datatableColumn)
  }
}

export {
  QTransition,
  QBtn,
  QIcon,
  sCard,
  sTree,
  sField,
  sInput,
  sSelect,
  sCheckbox,
  sCheckboxs,
  sRadio,
  sRadios,
  sDatetime,
  sUploader,
  sModal,
  paginator,
  datatable,
  datatableColumn,
  sLabel,
  sDialog
}

export default Embedded
