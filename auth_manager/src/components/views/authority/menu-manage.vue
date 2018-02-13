<template>
  <div class="menu-manage row" style="padding: 0 10px">
    <s-card class="col-5" color="darkblue" title="選單樹狀列表">
      <div slot="content">
        <q-scroll-area style="height: calc(100vh - 185px)">
          <s-tree ref="tree" :value="tree" @click="selectMenuTree"></s-tree>
        </q-scroll-area>
      </div>
    </s-card>
    <s-card class="col-7" color="darkblue" title="選單管理列表" style="padding-left: 10px">
      <div slot="content">
        <div class="row items-center" style="padding-bottom: 10px">
          <div class="col row justify-center xs-gutter">
            <div><s-btn color="darkred" @click="addMenu" :disable="menuTree.menuId === null">新增項目</s-btn></div>
            <div><s-btn color="darkred" @click="editMenu" :disable="!menu.menuId">修改項目</s-btn></div>
            <div><s-btn color="darkred" @click="delMenu" :disable="!menu.menuId">刪除項目</s-btn></div>
          </div>
        </div>
        <q-scroll-area style="height: calc(100vh - 230px)">
          <s-datatable ref="menuTable" :source="menuList" striped checkboxs @checked="selectMenu">
            <s-datatable-column slot="columns" v-for="(column, index) in columns" :key="index" :column="column"></s-datatable-column>
              <template slot="seqId" scope="props">
                <div class="row justify-center no-wrap">
                  <div><s-btn flat small icon="fa-angle-double-up" style="padding: 4px 0 0 8px" @click="moveTop(props.row, $event)"></s-btn></div>
                  <div><s-btn flat small icon="fa-angle-up" style="padding: 4px 0 0 8px" @click="moveUp(props.row, $event)"></s-btn></div>
                  <div><s-btn flat small icon="fa-angle-down" style="padding: 4px 0 0 8px" @click="moveDown(props.row, $event)"></s-btn></div>
                  <div><s-btn flat small icon="fa-angle-double-down" style="padding: 4px 0 0 8px" @click="moveBottom(props.row, $event)"></s-btn></div>
                </div>
              </template>
          </s-datatable>
        </q-scroll-area>
      </div>
    </s-card>
    <s-modal ref="menuModal" title="選單維護" width="400px" height="290px">
        <s-input slot="content" label="項目名稱" v-model="menuTemp.menuName"></s-input>
        <s-select slot="content" label="應用程式" v-model="menuTemp.applicationId" :options="apOptions"></s-select>
        <s-btn slot="action" color="darkred" big @click="saveMenu">儲存</s-btn>
        <s-btn slot="action" color="darkred" big @click="$refs.menuModal.close()">取消</s-btn>
    </s-modal>
  </div>
</template>

<script>
  import { viewMixin } from '../../mixins'
  import { QScrollArea, extend } from 'quasar'

  export default {
    name: 'MenuManage',
    mixins: [viewMixin],
    mounted () {
      this.searchBackup = extend({}, this.search)
      this.menuBackup = extend({}, this.menu)
      this.menuTree = extend({}, this.menu)
      this.findMenu(this.search)
      this.get('getAuthApplicationPairs', this.search, ap => {
        if (ap && ap.length > 0) {
          ap.forEach(a => { a.value = parseInt(a.value) })
          ap.unshift({ label: '目錄', value: null })
          this.apOptions = ap
        }
      })
    },
    data () {
      return {
        search: {
          module: 'SDB'
        },
        menuTree: {},
        menu: {
          menuId: null,
          parentMenuId: null,
          seqId: 0,
          menuName: '',
          applicationId: null,
          module: 'SDB'
        },
        searchBackup: {},
        menuBackup: {},
        menuTemp: {},
        menuList: [],
        apOptions: [],
        tree: [],
        columns: [
          { label: '類別',
            field: 'applicationId',
            width: '50px',
            formatter: val => {
              return val ? '應用程式' : '目錄'
            }},
          { label: '項目名稱', field: 'menuName', width: '100px' },
          { label: '順序調整', field: 'seqId', width: '80px' }
        ]
      }
    },
    methods: {
      makeTree (row, list) {
        let root = list.filter(l => l.parentMenuId === row.menuId)
        if (root && root.length > 0) {
          let clicked = this.menuTree.menuId === row.menuId
          let children = root.map(r => this.makeTree(r, list)).sort((a, b) => a.content.seqId - b.content.seqId)
          return { title: row.menuName, icon: 'fa-folder-open', content: row, children: children, clicked: clicked, expanded: true }
        }
        return { title: row.menuName, icon: row.applicationId ? 'fa-file-text' : 'fa-folder-open', content: row, children: [], clicked: false, expanded: true }
      },
      findMenu () {
        this.get('getAuthMenuList', this.searchBackup, r => {
          let root = { parentMenuId: null, menuId: 0, menuName: '首頁', seqId: 0, applicationId: null }
          let t = this.makeTree(root, r)
          this.tree = t instanceof Array ? t : [t]
        })
      },
      findSubMenu (menuTree) {
        if (menuTree.applicationId === null) {
          this.search.parentMenuId = menuTree.menuId
          this.get('getAuthMenuList', this.search, r => {
            r = r.sort((a, b) => a.seqId - b.seqId)
            this.menuList = r
          })
        }
      },
      selectMenuTree (menu) {
        this.menuList = []
        if (menu.content.menuId !== this.menuTree.menuId && menu.content.applicationId === null) {
          this.menuTree = menu.content
          this.findSubMenu(this.menuTree)
        }
        else {
          this.menuTree = extend({}, this.menuBackup)
          menu.clicked = false
        }
      },
      selectMenu () {
        let menu = this.$refs.menuTable.getChecked()
        this.menu = menu ? extend({}, menu) : extend({}, this.menuBackup)
      },
      addMenu () {
        this.menuTemp = extend({}, this.menuBackup)
        this.$refs.menuModal.open()
      },
      editMenu () {
        this.menuTemp = extend({}, this.menu)
        this.$refs.menuModal.open()
      },
      saveMenu () {
        if (!this.menuTemp.menuName) this.showError('請輸入選單名稱')
        else {
          if (!this.menuTemp.menuId) {
            // 新增項目
            this.menuTemp.seqId = this.menuList.length > 0 ? Math.max(...this.menuList.map(o => o.seqId)) + 1 : 1
            this.menuTemp.parentMenuId = this.menuTree.menuId
            this.menuList.push(this.menuTemp)
          }
          else {
            let menu = this.menuList.filter(m => m.menuId === this.menuTemp.menuId)[0]
            menu.menuName = this.menuTemp.menuName
            menu.applicationId = this.menuTemp.applicationId
          }
          this.put('saveAuthMenu', this.menuList, () => {
            this.$refs.menuModal.close()
            this.showSuccess('選單儲存成功', 3000)
            this.findMenu()
            this.findSubMenu(this.menuTree)
            this.menu = extend({}, this.menuBackup)
          })
        }
      },
      refreshMenu () {
        this.findMenu()
        this.findSubMenu(this.menuTree)
        this.menu = extend({}, this.menuBackup)
      },
      delMenu () {
        this.confirm('刪除選單', '是否確定刪除該選單？', () => {
          this.delete('deleteAuthMenu', this.menu, () => {
            this.showSuccess('選單刪除成功', 3000)
            this.refreshMenu()
          })
        })
      },
      moveTop (menu) {
        if (this.menuList === 1) return
        menu.seqId = 0
        this.reorderMenu()
      },
      moveUp (menu) {
        if (this.menuList === 1) return
        let ms = this.menuList.filter(m => m.seqId < menu.seqId).reverse()
        if (ms && ms.length > 0) {
          menu.seqId = ms[0].seqId + (ms[0].seqId = menu.seqId, 0)
          this.reorderMenu()
        }
      },
      moveDown (menu) {
        if (this.menuList === 1) return
        let ms = this.menuList.filter(m => m.seqId > menu.seqId)
        if (ms && ms.length > 0) {
          menu.seqId = ms[0].seqId + (ms[0].seqId = menu.seqId, 0)
          this.reorderMenu()
        }
      },
      moveBottom (menu) {
        if (this.menuList === 1) return
        menu.seqId = 99999
        this.reorderMenu()
      },
      reorderMenu () {
        let ms = this.menuList.sort((a, b) => a.seqId - b.seqId)
        ms.forEach((m, i) => { m.seqId = i + 1 })
        this.put('saveAuthMenu', ms, () => {
          this.refreshMenu()
        })
      }
    },
    components: {
      QScrollArea
    }
  }
</script>

<style>
  .menu-manage .q-card-actions {
    padding-left: 0;
    padding-right: 0;
  }
</style>
