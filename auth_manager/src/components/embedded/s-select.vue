<template>
  <q-field
    :icon="icon"
    :helper="helper"
    :error="invalidAndDirty"
    :error-label="errorLabel"
    :count="count"
    :dark="dark"
    :label-width="labelWidth"
    :inset="inset"
    label=""
  >
    <div v-if="label && label.length > 0" class="col row items-center justify-end" slot="label">
      <q-icon v-if="needFlag" name="fa-star" color="red" class="col-auto"></q-icon>
      <span class="col-auto">{{label}}</span>
    </div>
    <select-item class="col-auto"
      v-model="model"
      :options="options"
      :multiple="multiple"
      :radio="radio"
      :toggle="toggle"
      :chips="chips"
      :frameColor="frameColor"
      :filter="filter"
      :filter-placeholder="filterPlaceholder"
      :separator="separator"
      :display-value="displayValue"
      :prefix="prefix"
      :suffix="suffix"
      :float-label="floatLabel"
      :stack-label="stackLabel"
      :color="color"
      :inverted="inverted"
      :disable="disable"
      :error="error"
      :align="align"
      :before="before"
      :after="after"
      @change="change"
      @blur="$emit('blur')"
      @focus="$emit('focus')"
    ></select-item>
  </q-field>
</template>

<script>
  import { QField, QIcon } from 'quasar'
  import SelectItem from './select/select-item'

  export default {
    props: {
      value: {
        required: true
      },
      // QField
      label: String,
      icon: String,
      helper: String,
      dark: Boolean,
      count: {
        type: [Number, Boolean],
        default: false
      },
      labelWidth: {
        type: Number,
        default: 3,
        validator (val) {
          return val >= 1 && val < 12
        }
      },
      inset: {
        type: String,
        validator (val) {
          return ['icon', 'label', 'full'].includes(val)
        }
      },
      // QSelect
      options: Array,
      multiple: Boolean,
      radio: Boolean,
      toggle: Boolean,
      chips: Boolean,
      frameColor: String,
      filter: Boolean,
      filterPlaceholder: String,
      separator: {
        type: Boolean,
        default: true
      },
      displayValue: String,
      // Common
      prefix: String,
      suffix: String,
      floatLabel: String,
      stackLabel: String,
      color: String,
      inverted: Boolean,
      disable: Boolean,
      error: Boolean,
      noFlag: {
        type: Boolean,
        default: false
      },
      align: {
        type: String,
        default: 'left',
        validator: v => ['left', 'center', 'right'].includes(v)
      },
      before: {
        type: Array,
        validator: v => v.every(i => 'icon' in i)
      },
      after: {
        type: Array,
        validator: v => v.every(i => 'icon' in i)
      },
      // Validate
      v: Object,
      vError: Object
    },
    data () {
      return {}
    },
    computed: {
      needFlag () {
        return !this.noFlag && this.v && this.v.hasOwnProperty('required')
      },
      model: {
        get () {
          return this.value
        },
        set (val) {
          if (val !== this.value) {
            this.$emit('input', val)
          }
        }
      },
      errorLabel () {
        let error = ''
        if (this.vError && this.invalidAndDirty) {
          error = Object.keys(this.vError).filter(x => {
            return !this.v[x] && this.v.$dirty
          }).shift()
        }
        return typeof error !== 'undefined' && error !== '' ? this.vError[error] : ''
      },
      invalidAndDirty () {
        return this.v && this.v.$error
      }
    },
    methods: {
      change (value) {
        if (this.v) {
          var req = this.v.hasOwnProperty('required')
          if (value === '' && !req) {
            this.v.$reset()
          }
          else {
            this.v.$touch()
          }
        }
        this.$emit('change', value)
      }
    },
    components: {
      QField, SelectItem, QIcon
    }
  }
</script>

<style>
</style>
