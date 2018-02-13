<template>
  <th :style="columnStyles" @click="sort">
    <div class="datatable-column row" :layout="columnLayout">
      <div class="col" @mouseover="tooltipOpen()" @mouseleave="tooltipClose()">
        <slot>{{ cLabel || cId }}
          <q-popover v-if="cTooltip" ref="popover" self="bottom left" anchor="top left">
            <div style="background-color: #581715; color: white; border-radius: 3px; padding: 8px">{{cTooltip}}</div>
          </q-popover>
        </slot>
      </div>
      <div class="datatable-sort-arrow col-auto" :class="sortArrowClass" v-show="sorting"></div>
    </div>
  </th>
</template>

<script>
  import { QPopover } from 'quasar'
  const alignments = [
    'left',
    'center',
    'right',
    'auto'
  ]
  const sortClassMap = {
    '1': 'asc',
    '-1': 'desc'
  }
  export default {
    props: {
      column: {
        type: Object
      },
      id: {
        type: String
      },
      label: {
        type: String
      },
      width: {
        type: [Number, String]
      },
      alignment: {
        type: String,
        default: 'left',
        validator: value => {
          return alignments.indexOf(value) > -1
        }
      },
      formatter: {
        type: Function
      },
      sortable: {
        type: Boolean,
        default: false
      },
      isFixed: {
        type: Boolean,
        default: false
      },
      tooltip: {
        type: String
      }
    },
    data () {
      return {
        sortingDirection: 1
      }
    },
    computed: {
      cId () {
        return this.column ? this.column.field : this.id
      },
      cLabel () {
        return this.column ? this.column.label : this.label
      },
      cSortable () {
        return this.column ? this.column.sortable : this.sortable
      },
      cWidth () {
        return this.column ? this.column.width : this.width
      },
      cAlignment () {
        return this.column ? this.column.alignment : this.alignment
      },
      cFormatter () {
        return this.column ? this.column.formatter : this.formatter
      },
      cTooltip () {
        return this.column ? this.column.tooltip : this.tooltip
      },
      sorting: {
        get () {
          return (this.$parent.sortingId === this.cId && this.cSortable)
        },
        set (value) {
          if (value && this.cSortable) {
            this.$parent.setSortingInfo(this.cId, this.sortingDirection, this.cleanSort)
          }
        }
      },
      columnWidth () {
        if (!this.cWidth) {
          return
        }
        let suffix = isNaN(this.cWidth) ? '' : '%'
        return this.cWidth + suffix
      },
      columnLayout () {
        let direction = this.cAlignment !== 'right' ? 'row' : 'row-reverse'
        return direction + ' justify-center'
      },
      columnStyles () {
        let alignment = this.cAlignment === 'left' ? null : this.cAlignment
        return {
          width: this.columnWidth,
          textAlign: alignment
        }
      },
      sortArrowClass () {
        let direction = sortClassMap[this.sortingDirection]
        return 'datatable-sort-arrow-' + direction
      },
      template () {
        return this.$parent.$scopedSlots[this.cId]
      }
    },
    methods: {
      cleanSort () {
        this.sortingDirection = 1
        this.sorting = ''
        this.$parent.setSortingInfo(null, 1, null)
      },
      sort () {
        if (this.isFixed) {
          return
        }
        if (this.sorting) {
          this.sortingDirection *= -1
          this.sorting = this.sorting
          return
        }
        this.sorting = true
        this.$emit('sort', this.cId)
      },
      formatData (value, row) {
        if (!this.cFormatter) {
          return value
        }
        return this.cFormatter(value, row)
      },
      tooltipOpen () {
        console.log(this.cTooltip)
        if (this.cTooltip) {
          this.$refs.popover.open()
        }
      },
      tooltipClose () {
        if (this.tooltip) {
          this.$refs.popover.close()
        }
      }
    },
    components: {
      QPopover
    },
    created () {
      if (this.isFixed) {
        let addFixColumn = this.$parent.addFixColumn
        if (!addFixColumn) {
          console.warn('A datatable-column must be registered within a datatable component.')
          return
        }
        addFixColumn(this)
      }
      else {
        let addColumn = this.$parent.addColumn
        if (!addColumn) {
          console.warn('A datatable-column must be registered within a datatable component.')
          return
        }
        addColumn(this)
      }
    },
    destroyed () {
      if (this.isFixed) {
        this.$parent.removeFixColumn(this)
      }
      else {
        this.$parent.removeColumn(this)
      }
    }
  }
</script>

<style lang="scss">
  $sort-arrow-size: 0.375rem;
  $sort-arrow-offset: $sort-arrow-size / 2;

  .datatable-column {
    /* padding: 0.75rem 1rem; */
    padding: 8px 10px;
    cursor: pointer;
    user-select: none;
  }

  .datatable-sort-arrow {
    width: 0;
    height: 0;
    border: $sort-arrow-size solid transparent;
  }

  .datatable-sort-arrow-asc {
    border-bottom-color: currentColor;
    transform: translate(0, -$sort-arrow-offset);
  }

  .datatable-sort-arrow-desc {
    border-top-color: currentColor;
    transform: translate(0, $sort-arrow-offset);
  }
</style>
