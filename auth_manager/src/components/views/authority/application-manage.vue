<template>
  <div class="ap-manage" style="padding: 0 10px">
    <s-card color="darkblue" title="應用程式管理">
      <div slot="content" class="row md-gutter justify-center">
        <s-select label="模　　組" class="col-4" v-model="search.module" :options="moduleOptions"></s-select>
        <s-select label="應用程式" class="col-4" v-model="search.applicationId" :options="apOptions"></s-select>
      </div>
      <s-btn slot="action" color="darkred" @click="findAp">查詢</s-btn>
      <s-btn slot="action" color="darkred" @click="clearSearch">清除</s-btn>
    </s-card>
    <div class="row">
      <div class="col-5">
        <q-card>
          <div class="card-header row items-center">
            <q-card-title data-background-color="darkblue"><span style="color: white">應用程式設定</span></q-card-title>
            <span class="s-card-title col-auto"></span>
          </div>
          <q-card-actions align="center">
            <div class="s-card-actions" data-background-color="darkblue"></div>
            <s-btn color="darkred" big style="padding: 0 8px" @click="addAp">新增應用程式</s-btn>
            <s-btn color="darkred" big style="padding: 0 8px" @click="editAp" :disable="!ap.applicationId">編輯應用程式</s-btn>
            <s-btn color="darkred" big style="padding: 0 8px" @click="delAp" :disable="!ap.applicationId">刪除應用程式</s-btn>
          </q-card-actions>
          <q-card-separator/>
          <q-card-main style="padding-top: 10px">
            <q-scroll-area style="height: calc(100vh - 425px)">
              <s-datatable ref="apTable" :source="apList" striped checkboxs @checked="apChecked">
                <s-datatable-column slot="columns" v-for="(column, index) in apColumns" :key="index" :column="column"></s-datatable-column>
              </s-datatable>
            </q-scroll-area>
          </q-card-main>
        </q-card>
      </div>
      <div class="col" style="padding-left: 10px">
        <q-card>
          <div class="card-header row items-center">
            <q-card-title data-background-color="darkblue"><span style="color: white">功能限制設定</span></q-card-title>
            <span class="s-card-title col-auto"></span>
          </div>
          <q-card-actions align="center">
            <div class="s-card-actions" data-background-color="darkblue"></div>
            <s-btn color="darkred" big style="padding: 0 8px" @click="addItem" :disable="!ap.applicationId">新增功能限制</s-btn>
            <s-btn color="darkred" big style="padding: 0 8px" @click="editItem" :disable="!aItem.aItemId">編輯功能限制</s-btn>
            <s-btn color="darkred" big style="padding: 0 8px" @click="delItem" :disable="!aItem.aItemId">刪除功能限制</s-btn>
          </q-card-actions>
          <q-card-separator/>
          <q-card-main style="padding-top: 10px">
            <q-scroll-area style="height: calc(100vh - 425px)">
              <s-datatable ref="aItemTable" :source="aItemList" striped checkboxs @checked="itemChecked">
                <s-datatable-column slot="columns" v-for="(column, index) in itemColumns" :key="index" :column="column"></s-datatable-column>
              </s-datatable>
            </q-scroll-area>
          </q-card-main>
        </q-card>
      </div>
    </div>
    <s-modal ref="apModal" title="應用程式維護" width="600px" height="270px">
      <template slot="content">
        <s-select label="模組" v-model="apTemp.module" :options="moduleOptions"></s-select>
        <s-input  label="應用程式名稱" v-model="apTemp.applicationName" flag></s-input>
        <s-input  label="應用程式路徑" v-model="apTemp.route" flag></s-input>
      </template>
      <s-btn slot="action" color="darkred" big @click="saveAp">儲存</s-btn>
      <s-btn slot="action" color="darkred" big @click="$refs.apModal.close()">取消</s-btn>
    </s-modal>
    <s-modal ref="aItemModal" title="功能限制維護" width="440px" height="420px">
      <template slot="content">
        <s-select label="應用程式" v-model="aItemTemp.applicationId" :options="apOptions"></s-select>
        <s-select label="功能種類" v-model="aItemTemp.aItemType" :options="aItemTypeOptions"></s-select>
        <s-input  label="功能代碼" v-model="aItemTemp.aItemCode" flag></s-input>
        <s-input  label="功能名稱" v-model="aItemTemp.aItemName" flag></s-input>
        <s-input  label="功能參數" v-model="aItemTemp.aItemDefaultValue" type="textarea" :min-rows="5" :max-height="100"></s-input>
      </template>
      <s-btn slot="action" color="darkred" big @click="saveItem">儲存</s-btn>
      <s-btn slot="action" color="darkred" big @click="$refs.aItemModal.close()">取消</s-btn>
    </s-modal>
  </div>
</template>

<script>
  import { viewMixin } from '../../mixins'
  import { QCard, QCardTitle, QCardMain, QCardActions, QCardSeparator, QList, QItem, QItemMain, QScrollArea, extend } from 'quasar'

  export default {
    name: 'ApplicationManage',
    mixins: [viewMixin],
    mounted () {
      this.apBackup = extend({}, this.ap)
      this.aItemBackup = extend({}, this.aItem)
      this.get('getAuthLookupTypePairs', {type: 'Module'}, r => {
        if (r && r.length > 0) {
          this.search.module = r[0].value
          this.moduleOptions = r
          this.getLookup()
        }
      })
    },
    data () {
      return {
        search: {
          module: '',
          applicationId: null
        },
        ap: {
          module: '',
          applicationId: null,
          applicationName: '',
          route: ''
        },
        aItem: {
          applicationId: null,
          aItemId: null,
          aItemType: 'B',
          aItemCode: '',
          aItemName: '',
          aItemDefaultValue: ''
        },
        moduleOptions: [],
        apOptions: [],
        aItemTypeOptions: [
          { label: '按鈕', value: 'B' },
          { label: '欄位', value: 'F' },
          { label: '資料', value: 'D' },
          { label: '功能檢視', value: 'E' }
        ],
        apTemp: {},
        aItemTemp: {},
        apList: [],
        aItemList: [],
        apColumns: [
          { label: '模組名稱',
            field: 'module',
            width: '60px',
            sort: true,
            formatter: val => {
              let mo = this.moduleOptions.filter(m => m.value === val)
              return mo && mo.length > 0 ? mo[0].label : ''
            }},
          { label: '應用程式名稱', field: 'applicationName', width: '100px', sort: true }
        ],
        itemColumns: [
          { label: '種類',
            field: 'aItemType',
            width: '60px',
            sort: true,
            formatter: val => {
              let type = this.aItemTypeOptions.filter(a => a.value === val)
              return type && type.length > 0 ? type[0].label : ''
            }},
          { label: '功能代碼', field: 'aItemCode', width: '80px', sort: true },
          { label: '功能名稱', field: 'aItemName', width: '80px', sort: true },
          { label: '功能參數', field: 'aItemDefaultValue', width: '130px', sort: true }
        ]
      }
    },
    methods: {
      getLookup () {
        this.get('getAuthApplicationPairs', {module: this.search.module}, ap => {
          if (ap && ap.length > 0) {
            ap.forEach(a => { a.value = parseInt(a.value) })
            ap.unshift({ label: '請選擇應用程式', value: null })
            this.apOptions = ap
          }
          this.searchBackup = extend({}, this.search)
          this.findAp()
        })
      },
      findAp () {
        this.get('getAuthApplicationList', this.search, r => {
          this.apList = r
        })
        this.apBackup.module = this.search.module
        this.ap = extend({}, this.apBackup)
      },
      findAItem () {
        this.get('getAuthApplicationItemList', this.ap, r => {
          this.aItemList = r
        })
        this.aItemBackup.applicationId = this.ap.applicationId
        this.aItem = extend({}, this.aItemBackup)
      },
      clearSearch () {
        this.search = extend({}, this.apBackup)
        this.apList = this.aItemList = []
      },
      apChecked () {
        let ap = this.$refs.apTable.getChecked()
        this.ap = ap ? extend({}, ap) : extend({}, this.apBackup)
        if (ap) {
          this.findAItem()
        }
        else {
          this.aItemList = []
          this.aItem = extend({}, this.aItemBackup)
        }
      },
      addAp () {
        this.apTemp = extend({}, this.apBackup)
        this.$refs.apModal.open()
      },
      editAp () {
        this.apTemp = extend({}, this.ap)
        this.$refs.apModal.open()
      },
      saveAp () {
        if (!this.apTemp.applicationName) this.showError('請輸入應用程式名稱')
        else if (!this.apTemp.route) this.showError('請輸入應用程式路徑')
        else {
          this.put('saveAuthApplication', this.apTemp, () => {
            this.$refs.apModal.close()
            this.showSuccess('應用程式儲存成功', 3000)
            this.getLookup()
            this.findAp()
          })
        }
      },
      delAp () {
        this.confirm('刪除應用程式', '是否確定刪除該應用程式？', () => {
          this.delete('deleteAuthApplication', {applicationId: this.ap.applicationId}, () => {
            this.showSuccess('應用程式刪除成功', 3000)
            this.ap = extend({}, this.apBackup)
            this.getLookup()
            this.findAp()
          })
        })
      },
      itemChecked () {
        let aItem = this.$refs.aItemTable.getChecked()
        this.aItem = aItem ? extend({}, aItem) : extend({}, this.aItemBackup)
      },
      addItem () {
        this.aItemTemp = extend({}, this.aItemBackup)
        this.$refs.aItemModal.open()
      },
      editItem () {
        this.aItemTemp = extend({}, this.aItem)
        this.$refs.aItemModal.open()
      },
      saveItem () {
        if (!this.aItemTemp.aItemCode) this.showError('請輸入功能限制代碼')
        else if (!this.aItemTemp.aItemName) this.showError('請輸入功能限制名稱')
        else {
          this.put('saveAuthApplicationItem', this.aItemTemp, () => {
            this.$refs.aItemModal.close()
            this.showSuccess('功能限制儲存成功', 3000)
            this.findAItem()
          })
        }
      },
      delItem () {
        this.confirm('刪除功能限制', '是否確定刪除該功能限制？', () => {
          this.delete('deleteAuthApplicationItem', {aItemId: this.aItem.aItemId}, () => {
            this.showSuccess('功能限制刪除成功', 3000)
            this.aItem = extend({}, this.aItemBackup)
            this.findAItem()
          })
        })
      }
    },
    components: {
      QCard, QCardTitle, QCardMain, QCardActions, QCardSeparator, QList, QItem, QItemMain, QScrollArea
    }
  }
</script>

<style>
  .ap-manage .q-card-actions {
    padding-left: 0;
    padding-right: 0;
  }
</style>
