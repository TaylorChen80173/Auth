<template>
  <div class="datatable-collection">
    <table v-if="groupable" :class="{ 'table-striped': striped }">
      <tr v-for="(data, group, index) in groups" :key="index">
        <td class="datatable-group" :colspan="columnSpan">
          <div class="datatable-group-header row">
            <div self="size-x1" class="col">
              <span class="datatable-group-label" :style="indentStyle">{{ groupingColumn.formatData(group) }}</span>
            </div>
            <!--q-chip class="datatable-group-chip col-auto" color="orange-9" v-if="data.length > 1">{{ data.length }}</q-chip-->
          </div>
          <datatable-collection
            :rows="data"
            :columns="columns"
            :striped="striped"
            :editable="editable"
            :multiple="multiple"
            :checkboxs="checkboxs"
            :line-numbers="lineNumbers"
            :aggregated="aggregated"
            :margin="margin"
            :grouping-columns="groupingColumns"
            :grouping-index="groupingIndex + 1"
            :collection-index="collectionIndex * index"
            :optimize="optimize"
            :message="message">
          </datatable-collection>
        </td>
      </tr>
    </table>
    <table v-else class="datatable-resultset" :class="{ 'table-striped': striped }">
      <tr v-if="rows.length < 1">
        <td class="datatable-info-cell" :colspan="columnSpan">{{ message }}</td>
      </tr>
      <tr v-for="(row, index) in rows" :key="index">
        <td v-if="aggregated" class="datatable-cell datatable-aggregate-cell">&nbsp;</td>
        <datatable-cell v-for="column in columns" :key="column.cId" :column="column" :row="row" :editable="isEditable(index)"
                        :optimize="optimize"></datatable-cell>
        <!-- <td v-for="(column, cIndex) in columns" :key="column.id" class="datatable-cell" :style="column.columnStyles">
          <span>{{ typeof row[column.cId] === 'undefined' ? '' : column.formatData(row[column.cId], row) }}</span>
          <q-popover ref="popover" touch-position self="bottom left">
            {{typeof row[column.cId] === 'undefined' ? '' : column.formatData(row[column.cId], row)}}
          </q-popover>
        </td> -->
      </tr>
    </table>
  </div>
</template>

<script>
  import { QCheckbox, QPopover } from 'quasar'
  import DatatableCell from './datatable-cell.js'
  import groupBy from './utilities/group-by.js'

  export default {
    name: 'datatable-collection',
    props: {
      rows: {
        type: Array,
        required: true
      },
      columns: {
        type: Array,
        required: true
      },
      groupingColumns: {
        type: Array,
        default: []
      },
      groupingIndex: {
        type: Number,
        default: 0
      },
      striped: {
        type: Boolean,
        default: true
      },
      editable: {
        type: [Boolean, Number],
        default: false
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
      aggregated: {
        type: Boolean,
        default: false
      },
      margin: {
        type: String,
        default: '1.5em'
      },
      collectionIndex: {
        type: Number,
        default: 0
      },
      optimize: {
        type: Boolean,
        default: false
      },
      message: {
        type: String,
        default: '無資料顯示',
        required: false
      }
    },
    computed: {
      groupable () {
        return this.groupingIndex < this.groupingColumns.length
      },
      groupingColumn () {
        let columnId = this.groupingColumns[this.groupingIndex]
        return this.columns.find(column => column.cId === columnId)
      },
      groups () {
        let columnId = this.groupingColumn.cId
        return groupBy(this.rows, row => row[columnId])
      },
      columnSpan () {
        return this.columns.length + (this.lineNumbers ? 1 : 0)
      },
      indentStyle () {
        let margin = this.groupingIndex * 1.5
        return {
          'margin-left': margin + 'rem'
        }
      }
    },
    methods: {
      isEditable (index) {
        if (typeof this.editable === 'boolean') {
          return this.editable
        }
        else {
          return this.editable === this.collectionIndex + index + 1
        }
      }
    },
    components: {
      datatableCell: DatatableCell,
      QCheckbox,
      QPopover
    }
  }
</script>
