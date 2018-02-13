<template>
  <q-field :style="fieldStyle"
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
    <q-radio v-for="(op, index) in options" :key="index"
             style="padding-right: 10px"
      v-model="model"
      :checkedIcon="checkedIcon"
      :uncheckIcon="uncheckIcon"
      :color="color"
      :disable="disable"
      :label="op.label"
      :val="op.value"
      @change="change"
      @blur="$emit('blur')"
      @focus="$emit('focus')"
    ></q-radio>
  </q-field>
</template>

<script>
  import { QField, QRadio, QIcon } from 'quasar'

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
      /*
      fieldStyle: {
        type: Object,
        default: {} // { paddingRight: '10px' }
      },
      */
      fieldStyle: {
        type: [Object, Array],
        default: null // { paddingRight: '10px' }
      },
      // QCheckbox
      options: Array,
      checkedIcon: String,
      uncheckIcon: String,
      maxLength: Number,
      disable: Boolean,
      color: String,
      val: Object,
      v: Object,
      vError: Object
    },
    data () {
      return {}
    },
    computed: {
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
      },
      needFlag () {
        return !this.noFlag && this.v && this.v.hasOwnProperty('required')
      }
    },
    methods: {
      change (m) {
        this.$emit('change', m)
      }
    },
    components: {
      QField, QRadio, QIcon
    }
  }
</script>

<style>
</style>
