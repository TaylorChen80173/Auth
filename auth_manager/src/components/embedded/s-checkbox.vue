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
    <div class="row items-center s-checkbox">
      <span v-if="label && label.length > 0" slot="label" :class="['col-' + labelWidth]" style="text-align: right;padding-right: 8px">{{label}}</span>
      <q-checkbox
        class="col"
        v-model="model"
        :checkedIcon="checkedIcon"
        :uncheckIcon="uncheckIcon"
        :color="color"
        :disable="disable"
        :val="val"
        @change="change"
        @blur="$emit('blur')"
        @focus="$emit('focus')"
      ></q-checkbox>
    </div>
  </q-field>
</template>

<script>
  import { QField, QCheckbox, QIcon } from 'quasar'

  export default {
    props: {
      value: {
        type: [Boolean, Array],
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
          console.log(val)
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
      change (m) {
        this.$emit('change', m)
      }
    },
    components: {
      QField, QCheckbox, QIcon
    }
  }
</script>

<style>
</style>
