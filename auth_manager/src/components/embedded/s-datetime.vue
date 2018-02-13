<template>
  <q-field style="padding-right: 10px"
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
    <q-datetime
      ref="datetime"
      v-model="model"
      :type="type"
      :color="color !== 'primary' ? color : 'darkred'"
      :min="min"
      :max="max"
      :month-names="monthNames"
      :day-names="dayNames"
      :monday-first="mondayFirst"
      :saturday-first="saturdayFirst"
      :format24h="format24h"
      :format="format"
      :no-clear="noClear"
      :placeholder="placeholder"
      :clear-label="clearLabel"
      :ok-label="okLabel"
      :cancel-label="cancelLabel"
      :default-selection="defaultSelection"
      :display-value="displayValue"
      :inverted="inverted"
      :disable="disable"
      :error="error"
      :dark="dark"
      :before="before"
      :after="after"
      @change="change"
      @blur="$emit('blur')"
      @focus="$emit('focus')"
    ></q-datetime>
  </q-field>
</template>

<script>
  import { QField, QDatetime, QIcon } from 'quasar'

  function isDate (v) {
    return Object.prototype.toString.call(v) === '[object Date]'
  }
  const modelValidator = v => {
    const type = typeof v
    return (
      v === null || v === undefined ||
      type === 'number' || type === 'string' ||
      isDate(v)
    )
  }

  export default {
    props: {
      value: {
        validator: modelValidator,
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
      // QDatetime
      type: {
        type: String,
        default: 'date',
        validator (value) {
          return ['date', 'time', 'datetime'].includes(value)
        }
      },
      color: {
        type: String,
        default: 'primary'
      },
      min: {
        validator: modelValidator,
        default: '1900-01-01'
      },
      max: {
        validator: modelValidator,
        default: null
      },
      mondayFirst: Boolean,
      saturdayFirst: Boolean,
      format24h: Boolean,
      format: {
        type: String,
        default () {
          if (this.type === 'time') {
            return 'HH:mm'
          }
          else {
            return 'YYYY/MM/DD'
          }
        }
      },
      noClear: Boolean,
      placeholder: String,
      clearLabel: {
        type: String,
        default: '清除'
      },
      okLabel: {
        type: String,
        default: '確定'
      },
      cancelLabel: {
        type: String,
        default: '取消'
      },
      defaultSelection: [String, Number, Date],
      displayValue: String,
      inverted: Boolean,
      disable: Boolean,
      error: Boolean,
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
      vError: Object,
      noFlag: {
        type: Boolean,
        default: false
      }
    },
    mounted () {
      this.testColor(this.value)
    },
    data () {
      return {
        monthNames: [
          '一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'
        ],
        dayNames: [
          '日', '一', '二', '三', '四', '五', '六'
        ]
      }
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
          console.log(this.type)
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
      },
      testColor (val) {
        var el = this.$refs.datetime.$children[0].$vnode.elm.childNodes[1].firstElementChild
        if (el) {
          var c = el.classList.value
          c = c.replace(' text-grey', '')
          if (!val) {
            c += ' text-grey'
          }
          el.classList.value = c
        }
      }
    },
    watch: {
      value (val) {
        this.testColor(val)
      }
    },
    components: {
      QField, QDatetime, QIcon
    }
  }
</script>

<style>
</style>
