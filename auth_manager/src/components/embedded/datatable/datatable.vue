<template>
  <div>
    <div class="row no-wrap" style="border: 1px solid #EEEEEE;">
      <div class="datatable table-wrapper col-auto datatable-right-border" :class="tableClasses">
        <table :style="{ width: (checkboxs ? 50 : 0) + (lineNumbers ? lineColumnWidth : 0) + 'px' }" :class="{ 'table-striped': striped }">
          <thead class="datatable-columns">
            <tr>
              <th v-if="checkboxs" style="width: 50px">
                <div class="datatable-column datatable-linenumber-column" style="padding-left: 16px">
                  <q-checkbox v-if="multiple" v-model="checkedAll" @input="doCheckAll"></q-checkbox>&nbsp;
                </div>
              </th>
              <th v-if="lineNumbers" :style="{ width: lineColumnWidth + 'px', lineHeight: lineHeight2 }">
                <div class="datatable-column datatable-linenumber-column">{{ lineLabel }}</div>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(row, index) in rows" :key="index">
              <td v-if="checkboxs" class="datatable-cell datatable-linenumber-cell" style="width: 50px" :style="{lineHeight: lineHeight2}">
                <q-checkbox v-model="checked" :val="row" @change="checkChanged"></q-checkbox>
              </td>
              <td v-if="lineNumbers" class="datatable-cell datatable-linenumber-cell" :style="{ 'width': lineColumnWidth + 'px', lineHeight: lineHeight2 }">
                {{ offset + index + 1 }}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="datatable table-wrapper col" :class="tableClasses">
        <table>
          <thead class="datatable-columns">
            <tr>
              <slot name="columns"></slot>
            </tr>
          </thead>
          <tbody class="datatable-collections">
            <tr>
              <td class="datatable-group" :colspan="columnSpan">
                <div class="datatable-collection">
                  <table class="datatable-resultset" :class="{ 'table-striped': striped }">
                    <tr v-if="rows.length < 1">
                      <td class="datatable-info-cell" :colspan="columnSpan" :style="{lineHeight: lineHeight}">{{ message }}</td>
                    </tr>
                    <tr v-for="(row, index) in rows" :key="index">
                      <td v-for="(column, cIndex) in columns" :key="column.cId" class="datatable-cell" :style="columnStyles(column)">
                        <slot :name="column.cId" :column="column" :row="row" :index="index">
                          <div>{{ typeof row[column.cId] === 'undefined' ? '' : column.formatData(row[column.cId], row) }}</div>
                          <q-popover ref="popover" touch-position self="bottom left" style="padding: 3px; border-radius: 3px; color: white; background-color: #193B5B">
                            {{typeof row[column.cId] === 'undefined' ? '' : column.formatData(row[column.cId], row)}}
                          </q-popover>
                        </slot>
                      </td>
                    </tr>
                  </table>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="datatable table-wrapper col-auto datatable-left-border" :class="tableClasses" v-if="$slots['fixColumns']">
        <table :style="{ width: fixedWidth }" :class="{ 'table-striped': striped }">
          <thead class="datatable-columns">
            <tr>
              <slot name="fixColumns"></slot>
            </tr>
          </thead>
          <tbody class="datatable-collections" v-if="groupingColumnIds.length > 0">
            <tr><td>&nbsp;</td></tr>
            <template v-for="(r, index) in groupIndexing()">
              <tr v-if="r.num === 0">
                <template v-for="column in fixColumns">
                  <td :style="{lineHeight: lineHeight}"><slot :name="'fixGroupCell-' + column.id" :row="r.row" :index="index"></slot></td>
                </template>
              </tr>
              <tr v-else>
                <template v-for="column in fixColumns">
                  <td :style="{lineHeight: lineHeight}"><slot :name="'fixCell-' + column.id" :row="r.row" :index="index"></slot></td>
                </template>
              </tr>
            </template>
          </tbody>
          <tbody class="datatable-collections" v-else>
            <tr v-for="(row, index) in rows" :key="index">
              <template v-for="column in fixColumns">
                <td class="datatable-cell" :style="{lineHeight: lineHeight}"><div><slot :name="'fixCell-' + column.cId" :row="rows[index]" :index="index"></slot>&nbsp;</div></td>
              </template>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
  import { QCheckbox, QChip, QPopover, extend } from 'quasar'
  import sortBy from './utilities/sort-by.js'

  export default {
    props: {
      fixed: {
        type: Boolean,
        default: true
      },
      striped: {
        type: Boolean,
        default: true
      },
      source: {
        type: Array,
        default: () => []
      },
      multiple: {
        type: Boolean,
        default: false
      },
      checkboxs: {
        type: Boolean,
        default: false
      },
      lineNumbers: {
        type: Boolean,
        default: false
      },
      lineLabel: {
        type: String,
        default: '#'
      },
      message: {
        type: String,
        default: '無資料顯示'
      },
      offset: {
        type: Number,
        default: 0
      },
      edit: {
        type: Boolean,
        default: false
      }
    },
    data () {
      return {
        columns: [],
        fixColumns: [],
        filter: null,
        sortingId: null,
        sortingDirection: 1,
        sortingClean: null,
        groupingColumnIds: [],
        groupingDropzoneActive: false,
        checkedAll: false,
        checked: [],
        oldChecked: []
      }
    },
    computed: {
      lineHeight () {
        return this.edit ? '28px' : '22px'
      },
      lineHeight2 () {
        return this.edit ? '30px' : '22px'
      },
      sortingColumn () {
        return this.columns.find(column => column.cId === this.sortingId)
      },
      groupingColumns () {
        return this.groupingColumnIds.map(columnId => {
          return this.columns.find(column => column.cId === columnId)
        })
      },
      tableClasses () {
        return {
          'datatable-editable': this.editable,
          'table-fixed': this.fixed
        }
      },
      groupableColumns () {
        return this.columns.filter(column => column.cGroupable)
      },
      rows () {
        let rows = this.source
        if (!this.$parent.url) {
          // Sort the filtered set
          if (this.sortingColumn) {
            rows = sortBy(rows, row => row[this.sortingColumn.cId], this.sortingColumn.sortingDirection)
          }
        }
        if (!rows) {
          rows = []
        }
        return rows
      },
      columnSpan () {
        let columnSpan = this.columns.length
        return columnSpan
      },
      lineColumnWidth () {
        let count = 0
        if (this.lineLabel !== '#') {
          count = this.lineLabel.length + this.offset
        }
        else {
          count = this.rows && this.rows.length ? this.rows.length.toString().length + this.offset : 1
        }
        return (count + 2) * 23
      },
      optimize () {
        return this.source.length >= this.threshold
      },
      fixedWidth () {
        return this.fixColumns.reduce((s, c) => {
          return parseInt(c.cWidth.toLowerCase().replace('px', '')) + s
        }, 0)
      }
    },
    methods: {
      columnStyles (column) {
        let style = column.columnStyles
        style.lineHeight = this.lineHeight
        return style
      },
      setSortId (id) {
        if (this.$parent.url) {
          this.$parent.setSortId(id)
        }
      },
      setSortingInfo (id, direction, clean) {
        this.sortingId = id
        this.sortingDirection = direction
        this.sortingClean = clean
        if (this.$parent.url) {
          this.$parent.setSortingInfo(id, direction, clean)
        }
      },
      addColumn (column) {
        this.columns.push(column)
      },
      removeColumn (column) {
        let index = this.columns.indexOf(column)
        this.columns.splice(index, 1)
      },
      addFixColumn (column) {
        this.fixColumns.push(column)
      },
      removeFixColumn (column) {
        let index = this.fixColumns.indexOf(column)
        this.fixColumns.splice(index, 1)
      },
      doCheckAll (checked) {
        if (checked) {
          let range = []
          for (let i = 0; i < this.rows.length; i++) {
            range.push(this.rows[i])
          }
          this.checked = range
        }
        else {
          this.checked = []
        }
        this.oldChecked = this.checked
        this.$emit('checked', this.checked, this.checked.length > 0)
      },
      checkChanged (val) {
        let isChecked = val.length > this.oldChecked.length
        if (!this.multiple) {
          let changed = val.filter((p, idx) => {
            return this.oldChecked.length === 0 ? true
              : this.oldChecked.some(c => {
                return Object.keys(p).some(prop => {
                  return p[prop] !== c[prop]
                })
              })
          })
          this.checked = changed
        }
        else {
          if (this.checkedAll && this.checked.length !== this.rows.length) {
            this.checkedAll = false
          }
          else if (!this.checkedAll && this.checked.length === this.rows.length) {
            this.checkedAll = true
          }
        }
        this.oldChecked = extend(true, [], this.checked)
        this.$emit('checked', this.multiple ? this.checked : this.checked[0], isChecked)
      },
      getChecked () {
        return this.multiple ? this.checked : this.checked[0]
      }
    },
    watch: {
      source (val) {
        this.checkedAll = false
        this.checked = []
        this.oldChecked = []
      }
    },
    components: {
      QCheckbox, QChip, QPopover
    }
  }
</script>
<style lang="scss">
  @import "styles/datatable";
</style>
