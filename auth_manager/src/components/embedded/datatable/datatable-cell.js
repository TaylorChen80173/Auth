// const defaultTemplate = '<div>{{ column.formatData(row[column.cId], row) }}</div>'
const defaultTemplate = `<div>{{ typeof row[column.cId] === 'undefined' ? '' : column.formatData(row[column.cId], row) }}</div>`
const editableTemplate = '<input v-model="row[column.cId]"/>'
const optimizedEditableTemplate = `<input v-model.lazy="typeof row[column.cId] === 'undefined' ? row[column.cId] : ''"/>`

function getChildComponent (editable, optimize) {
  let component = {
    template: defaultTemplate,
    props: ['row', 'column']
  }
  if (editable) {
    component.template = optimize ? optimizedEditableTemplate : editableTemplate
  }
  return component
}

export default {
  functional: true,
  name: 'datatable-cell',
  props: {
    row: Object,
    column: Object,
    editable: {
      type: Boolean,
      default: false
    },
    optimize: {
      type: Boolean,
      default: false
    }
  },
  render (createElement, context) {
    let row = context.props.row
    let column = context.props.column

    let cell = 'td'
    let cellProps = {
      class: {
        'datatable-cell': true
      },
      style: column.columnStyles
    }
    if (column.template) {
      let vNode = column.template({
        row,
        column,
        value: row[column.cId]
      })
      return createElement(cell, cellProps, [vNode])
    }
    let child = getChildComponent(context.props.editable, context.props.optimize)
    let childProps = {
      props: {
        row,
        column
      }
    }
    return createElement(cell, cellProps, [
      createElement(child, childProps)
    ])
  }
}
