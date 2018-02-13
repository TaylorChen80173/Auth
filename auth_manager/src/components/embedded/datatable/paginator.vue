<template>
  <div>
    <div class="paginator-body">
      <slot :data="data" :offset="index * actualPageSize" :order-columns="orderColumns" :page-number="pageNumber"></slot>
    </div>
    <div class="paginator row wrap items-center">
      <div style="width: 130px; padding-left: 10px">
        <q-select v-model="actualPageSize" align="right" suffix="行/頁" :options="options"></q-select>
      </div>
      <div class="pagination col justify-center">
        <li v-show="totalPages > 0">
          <a href="javascript:" class="pagination__navigation" :class="{ 'pagination__navigation--disabled': pageNumber === 1 }" @click="movePrevious">
            <q-icon mat="chevron_left" size="2rem" />
          </a>
        </li>
        <li v-for="(num, index) in items" :key="index">
          <span v-if="isNaN(num)" class="pagination__more">{{ num }}</span>
          <template v-else>
            <a href="javascript:" class="pagination__item" :class="{ 'pagination__item--active': num === pageNumber }" @click="moveTo(num)">
              <span>{{ num }}</span>
            </a>
          </template>
        </li>
        <li v-show="totalPages > 0">
          <a href="javascript:" class="pagination__navigation" :class="{ 'pagination__navigation--disabled': pageNumber === totalPages }" @click="moveNext">
            <q-icon mat="chevron_right" size="2rem" />
          </a>
        </li>
      </div>
      <div class="self-center" style="padding-right: 10px">
        <span>{{ totalInfo }}</span>
      </div>
    </div>
  </div>
</template>

<script>
  import filterBy from './utilities/filter-by.js'
  import page from './utilities/page.js'
  import { viewMixin } from '../../mixins'
  import { QIcon, QSelect, extend, date } from 'quasar'

  export default {
    mixins: [viewMixin],
    props: {
      url: {
        type: String
      },
      source: {
        type: Array,
        default: () => []
      },
      pageSize: {
        type: Number,
        default: 10
      },
      filter: {
        type: String
      }
    },
    data () {
      return {
        index: 0,
        orderColumns: '',
        orderClean: null,
        searchModel: {},
        pageData: [],
        totalPages: 0,
        totalLines: 0,
        innerPageSize: null,
        loaded: false,
        preMount: false
      }
    },
    computed: {
      totalInfo () {
        let start = this.index * this.actualPageSize
        return (this.data && this.data.length > 0) ? ((start + 1) + '-' + (start + this.data.length) + ' 全部 ' + this.totalLines + ' 行') : ''
      },
      actualPageSize: {
        get () {
          return this.innerPageSize ? this.innerPageSize : this.pageSize
        },
        set (value) {
          this.innerPageSize = value
          // this.getData(this.searchModel)
          this.find(this.searchModel)
        }
      },
      options () {
        return Array.apply(null, { length: 6 }).map((v, index) => {
          let value = 5 * (index + 1)
          return { label: value.toString(), value: value }
        })
      },
      pages () {
        let data = this.source
        if (this.filter) {
          data = filterBy(data, this.filter)
        }
        let pages = page(data, this.actualPageSize)
        // need to reset the page number if the data length changes
        // otherwise the index will be outside the bounds of the data
        if (this.pageNumber > pages.length) {
          this.pageNumber = 1
        }
        this.totalPages = pages.length
        this.totalLines = this.source.length
        return pages
      },
      pageNumber: {
        get () {
          return this.index + 1
        },
        set (value) {
          this.index = value - 1
          this.$emit('page-changed', value)
        }
      },
      items () {
        let length = this.url ? this.totalPages : this.pages.length
        if (length <= 5) {
          return this.range(1, length)
        }
        let min = this.index - 3
        min = min > 0 ? min : 1

        let max = min + 6
        max = max <= length ? max : length

        if (max === length) {
          min = length - 6
        }
        const range = this.range(min, max)

        if (this.index >= 4 && length > 6) {
          range.splice(0, 2, 1, '...')
        }
        if (this.index + 3 < length && length > 6) {
          range.splice(range.length - 2, 2, '...', length)
        }
        return range
      },
      data () {
        return this.url ? this.pageData : this.pages[this.index]
      }
    },
    methods: {
      range (from, to) {
        const range = []
        from = from > 0 ? from : 1
        for (let i = from; i <= to; i++) {
          range.push(i)
        }
        return range
      },
      movePrevious () {
        this.pageNumber -= this.pageNumber > 1 ? 1 : 0
        this.getData(this.searchModel)
      },
      moveNext () {
        this.pageNumber += (this.pageNumber !== (this.url ? this.totalPages : this.pages.length)) ? 1 : 0
        this.getData(this.searchModel)
      },
      moveTo (pageNumber) {
        if (pageNumber > 0 && pageNumber <= (this.url ? this.totalPages : this.pages.length)) {
          this.pageNumber = pageNumber
          this.getData(this.searchModel)
        }
      },
      search (searchModel) {
        this.clean()
        this.getData(searchModel)
      },
      getData (searchModel) {
        if (this.url) {
          this.searchModel = searchModel
          let model = extend({}, searchModel, { page: this.index, size: this.actualPageSize, sort: this.orderColumns })
          for (var key in model) {
            if (model[key] instanceof Date) {
              model[key] = date.formatDate(model[key], 'YYYY-MM-DD')
            }
          }
          this.get(this.url, model, r => {
            this.loaded = true
            this.pageData = r.content
            this.pageNumber = r.number + 1
            this.totalPages = r.totalPages
            this.totalLines = r.totalElements
            this.$emit('get', 'getData')
            if (r.content.length === 0) {
              // Toast.create.warning({
              //   html: '查無資料，請重新輸入查詢條件。',
              //   timeout: 2000
              // })
              this.app.openDialog({
                title: '提示訊息',
                message: '查無資料，請重新輸入查詢條件。',
                icon: 'fa-exclamation',
                color: '#3399ff',
                btnColor: 'blue',
                cancelText: '確認'
              })
            }
          })
        }
      },
      find (searchModel) {
        if (!this.preMount) {
          this.index = 0
        }
        this.getData(searchModel)
        this.preMount = false
      },
      setSortId (id) {
        this.orderColumns = id + ',' + 'asc'
      },
      setSortingInfo (id, direction, clean) {
        this.orderColumns = id ? id + ',' + (direction === 1 ? 'asc' : 'desc') : ''
        this.orderClean = clean
        if (clean) {
          this.getData(this.searchModel)
        }
      },
      getPageData () {
        return this.data
      },
      clean () {
        this.pageData = []
        this.index = 0
        this.orderColumns = ''
        this.totalPages = 0
        this.totalLines = 0
        this.innerPageSize = null
        this.loaded = false
        this.preMount = false
        if (this.orderClean) {
          this.orderClean()
        }
      }
    },
    components: {
      QIcon, QSelect
    }
  }
</script>

<style lang="stylus" scoped>
  shadow-key-umbra-opacity = rgba(0,0,0, .2)
  shadow-key-penumbra-opacity = rgba(0,0,0, .14)
  shadow-ambient-shadow-opacity = rgba(0,0,0, .12)

  .paginator
    width: 100%
    height: 60px
    background-color: #FAFAFC;
    /* border-top: 1px solid #DDDDEE; */
    box-shadow: 0 1px 4px 0 rgba(0, 0, 0, 0.14);
    border-radius: 3px;

    .pagination
      align-items: center
      align-items: center
      display: inline-flex
      list-style-type: none
      height: 60px
      margin: 0
      overflow-x: auto
      overflow-y: hidden
      max-width: 100%
      padding: 0
      padding: 1rem;

      > li
        align-items: center
        display: flex

      a
        transition: .3s cubic-bezier(0.0, 0.0, 0.2, 1)

        &:hover
          box-shadow: 0 1px 5px shadow-key-umbra-opacity,
                      0 2px 2px shadow-key-penumbra-opacity,
                      0 3px 1px -2px shadow-ambient-shadow-opacity

      &--circle
        .pagination__item,
        .pagination__more,
        .pagination__navigation
          border-radius: 50%

      &--disabled
        pointer-events: none
        opacity: .6

      &__item
        box-shadow: 0 1px 5px shadow-key-umbra-opacity,
                    0 2px 2px shadow-key-penumbra-opacity,
                    0 3px 1px -2px shadow-ambient-shadow-opacity
        border-radius: 4px
        color: #000
        display: inline-flex
        justify-content: center
        align-items: center
        background: transparent
        height: 34px
        width: 34px
        margin: .3rem
        text-decoration: none

        &--active
          box-shadow: 0 2px 4px -1px shadow-key-umbra-opacity,
                      0 4px 5px shadow-key-penumbra-opacity,
                      0 1px 10px shadow-ambient-shadow-opacity
          // background: #1976D2
          background: #213649
          color: #fff

      &__navigation
        box-shadow: 0 1px 5px shadow-key-umbra-opacity,
                    0 2px 2px shadow-key-penumbra-opacity,
                    0 3px 1px -2px shadow-ambient-shadow-opacity
        display: inline-flex
        justify-content: center
        align-items: center
        text-decoration: none
        color: rgba(#000, .87)
        height: 2rem

        border-radius: 4px
        width: 2rem
        margin: .3rem 15px

        .icon
          font-size: 1.5rem
          transition: 0.2s cubic-bezier(0.4, 0.0, 0.6, 1)
          vertical-align: middle
          color: rgba(#000, .54)

        &--disabled
          opacity: .6
          pointer-events: none

      &__more
        margin: .3rem
        display: inline-flex
        align-items: flex-end
        justify-content: center
        height: 2rem
        width: 2rem
</style>
