<template>
  <q-input-frame
    ref="input"
    class="q-select"

    :prefix="prefix"
    :suffix="suffix"
    :stack-label="stackLabel"
    :float-label="floatLabel"
    :error="error"
    :disable="disable"
    :inverted="inverted"
    :dark="dark"
    :before="before"
    :after="after"
    :color="frameColor || memColor"

    :focused="focused"
    focusable
    :length="length"
    :additional-length="additionalLength"

    @click.native="open"
    @focus.native="__onFocus"
    @blur.native="__onBlur"
  >
    <div
      v-if="hasChips"
      class="col row items-center group q-input-chips"
      :class="alignClass"
    >
      <q-chip
        v-for="{label, value} in selectedOptions"
        :key="label"
        small
        :closable="!disable"
        :color="memColor"
        @click.native.stop
        @close="__toggle(value)"
      >
        {{ label }}
      </q-chip>
    </div>

    <div
      v-else
      class="col row items-center q-input-target"
      :class="alignClass"
      v-html="actualValue"
      :style="{color: memColor}"
    ></div>

    <q-icon slot="after" name="arrow_drop_down" class="q-if-control"></q-icon>

    <q-popover
      ref="popover"
      fit
      :disable="disable"
      :offset="[0, 10]"
      :anchor-click="false"
      class="column no-wrap"
      @open="__onFocus"
      @close="__onClose"
    >
      <q-field-reset>
        <q-search ref="searchField"
          v-if="filter"
          v-model="terms"
          @input="reposition"
          :placeholder="filterPlaceholder"
          :debounce="100"
          :color="memColor"
          class="no-margin"
          :after="controlAfter"
          style="padding: 6px"
        ></q-search>
      </q-field-reset>

      <q-list
        link
        :separator="separator"
        class="no-border scroll"
      >
        <template v-if="multiple">
          <q-item-wrapper
            v-for="opt in visibleOptions"
            :key="JSON.stringify(opt)"
            :cfg="opt"
            slot-replace
            @click.capture="__toggle(opt.value)"
          >
            <q-toggle
              v-if="toggle"
              slot="right"
              :color="memColor"
              :value="optModel[opt.index]"
            ></q-toggle>
            <q-checkbox
              v-else
              slot="left"
              :color="memColor"
              :value="optModel[opt.index]"
            ></q-checkbox>
          </q-item-wrapper>
        </template>
        <template v-else>
          <q-item-wrapper
            v-for="opt in visibleOptions"
            :key="JSON.stringify(opt)"
            :cfg="opt"
            slot-replace
            :active="value === opt.value"
            @click.capture="__select(opt)"
          >
            <q-radio
              v-if="radio"
              :color="memColor"
              slot="left"
              :value="value"
              :val="opt.value"
            ></q-radio>
          </q-item-wrapper>
        </template>
      </q-list>
    </q-popover>
  </q-input-frame>
</template>

<script>
  import { QFieldReset, QPopover, QSearch, QList, QItemWrapper, QCheckbox, QRadio, QToggle, clone } from 'quasar'
  import SelectMixin from './select-mixin'

  function defaultFilterFn (terms, obj) {
    return (typeof obj.selectable === 'undefined' || (obj.selectable && obj.value !== null)) && obj.label.toLowerCase().indexOf(terms) > -1
  }
  export default {
    name: 'select-item',
    mixins: [SelectMixin],
    mounted () {
      this.memColor = this.value ? 'black' : 'grey'
    },
    components: {
      QFieldReset,
      QSearch,
      QPopover,
      QList,
      QItemWrapper,
      QCheckbox,
      QRadio,
      QToggle
    },
    props: {
      filter: [Function, Boolean],
      filterPlaceholder: {
        type: String,
        default: '過濾'
      },
      radio: Boolean,
      placeholder: String,
      separator: Boolean
    },
    computed: {
      optModel () {
        if (this.multiple) {
          return this.options.map(opt => this.value.includes(opt.value))
        }
      },
      visibleOptions () {
        let opts = clone(this.options).map((opt, index) => {
          opt.index = index
          opt.value = this.options[index].value
          return opt
        })
        if (this.filter && this.terms.length) {
          const lowerTerms = this.terms.toLowerCase()
          opts = opts.filter(opt => this.filterFn(lowerTerms, opt))
        }
        return opts
      },
      filterFn () {
        return typeof this.filter === 'boolean'
          ? defaultFilterFn
          : this.filter
      },
      activeItemSelector () {
        return this.multiple
          ? `.q-item-side > ${this.toggle ? '.q-toggle' : '.q-checkbox'} > .active`
          : `.q-item.active`
      },
      controlAfter () {
        return [{
          icon: null,
          content: false,
          handler: this.clearAndFocus
        }]
      }
    },
    methods: {
      open (event) {
        if (!this.disable) {
          this.$refs.popover.open()
        }
      },
      close () {
        this.$refs.popover.close()
      },
      reposition () {
        const popover = this.$refs.popover
        if (popover.opened) {
          popover.reposition()
        }
      },
      __onFocus () {
        this.focused = true
        this.$emit('focus')
        const selected = this.$refs.popover.$el.querySelector(this.activeItemSelector)
        if (selected) {
          selected.scrollIntoView()
        }
      },
      __onBlur (e) {
        this.__onClose()
        setTimeout(() => {
          const el = document.activeElement
          if (el !== document.body && !this.$refs.popover.$el.contains(el)) {
            this.close()
          }
        }, 1)
      },
      __onClose () {
        this.focused = false
        this.$emit('blur')
        this.terms = ''
      },
      __select (opt) {
        if ((typeof opt.selectable === 'undefined' || opt.selectable) && this.value !== opt.value) {
          this.$emit('input', opt.value)
          this.$emit('change', opt.value)
        }
        this.close()
      },
      clearAndFocus () {
        this.$refs.searchField.clear()
        this.$refs.searchField.focus()
      }
    },
    watch: {
      value (val) {
        this.memColor = val ? 'black' : 'grey'
      }
    }
  }
</script>
