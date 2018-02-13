<template>
  <div class="user-manage" style="padding: 0 10px">
    <s-card color="darkblue" title="帳號管理">
      <div slot="content" class="row md-gutter justify-center">
        <s-input label="帳號" class="col-4" v-model="search.userAccount"></s-input>
        <s-input label="顯示名稱" class="col-4" v-model="search.displayName"></s-input>
      </div>
      <s-btn slot="action" color="darkred" @click="addUser">新增帳號</s-btn>
      <s-btn slot="action" color="darkred" @click="editUser" :disable="!user.userId">編輯帳號</s-btn>
      <s-btn slot="action" color="darkred" @click="delUser" :disable="!user.userId">刪除帳號</s-btn>
      <s-btn slot="action" color="darkred" @click="findUser">查詢</s-btn>
      <s-btn slot="action" color="darkred" @click="clearSearch">清除</s-btn>
    </s-card>
    <s-paginator ref="pager" url="getAuthUserList" :page-size="10" style="padding-bottom: 20px">
      <template scope="page">
        <s-datatable ref="userTable" :source="page.data" :offset="page.offset" striped line-numbers checkboxs @checked="userChecked">
          <s-datatable-column slot="columns" v-for="(column, index) in columns" :key="index" :column="column"></s-datatable-column>
        </s-datatable>
      </template>
    </s-paginator>
    <s-modal ref="userModal" title="帳號維護" width="800px" height="550px">
      <template slot="content">
        <q-tabs ref="tabs" color="deepblue" align="center">
          <q-tab default slot="title" name="tab-user" label="帳號維護"/>
          <q-tab slot="title" name="tab-group" label="所屬群組"/>
          <q-tab-pane name="tab-user">
            <div class="row justify-center"><s-input  class="col-8" label="帳號" v-model="userTemp.userAccount" flag></s-input></div>
            <div class="row justify-center"><s-input  class="col-8" label="顯示名稱" v-model="userTemp.displayName" flag></s-input></div>
            <div class="row justify-center"><s-select class="col-8" label="帳號來源" v-model="userTemp.sourceType" :options="sourceTypeOptions"></s-select></div>
            <div class="row justify-center"><s-input  class="col-8" label="Email" v-model="userTemp.email" flag></s-input></div>
            <div class="row justify-center"><s-select class="col-8" label="是否生效" v-model="userTemp.isActive" :options="isActiveOptions" v-if="userTemp.userId"></s-select></div>
            <div class="row justify-center"><s-input  class="col-8" label="備註" v-model="userTemp.memo" type="textarea" :min-rows="5" :max-height="100"></s-input></div>
          </q-tab-pane>
          <q-tab-pane name="tab-group">
            <div class="row justify-between md-gutter">
              <s-card color="darkblue" title="<div class='full-width text-center'>已選群組</div>" title-align="full-width" class="col-5">
                <q-scroll-area slot="content" style="height: 250px">
                  <q-list hightlight link separator striped>
                    <q-item v-for="(g, index) in groupList1" :key="index" @click="selectGroup1(g, $event)">
                      <q-item-main :label="g.groupName" :style="{color: group1.groupName === g.groupName ? 'red' : 'black'}"></q-item-main>
                    </q-item>
                  </q-list>
                </q-scroll-area>
              </s-card>
              <s-card color="darkblue" title="　" class="col">
                <div slot="content" style="height: 250px">
                  <div class="row text-center items-center justify-center" style="height: 25%"><s-btn flat icon="fa-angle-right" style="padding-right: 5px" @click="moveRight"></s-btn></div>
                  <div class="row text-center items-center justify-center" style="height: 25%"><s-btn flat icon="fa-angle-double-right" style="padding-right: 5px" @click="moveAllRight"></s-btn></div>
                  <div class="row text-center items-center justify-center" style="height: 25%"><s-btn flat icon="fa-angle-double-left" style="padding-right: 5px" @click="moveAllLeft"></s-btn></div>
                  <div class="row text-center items-center justify-center" style="height: 25%"><s-btn flat icon="fa-angle-left" style="padding-right: 5px" @click="moveLeft"></s-btn></div>
                </div>
              </s-card>
              <s-card color="darkblue"  title="<div class='full-width text-center'>未選群組</div>" title-align="full-width" class="col-5">
                <q-scroll-area slot="content" style="height: 250px">
                  <q-list hightlight link separator striped>
                    <q-item v-for="(g, index) in groupList2" :key="index" @click="selectGroup2(g, $event)">
                      <q-item-main :label="g.groupName" :style="{color: group2.groupName === g.groupName ? 'red' : 'black'}"></q-item-main>
                    </q-item>
                  </q-list>
                </q-scroll-area>
              </s-card>
            </div>
          </q-tab-pane>
        </q-tabs>
      </template>
      <s-btn slot="action" color="darkred" big @click="saveUser">儲存</s-btn>
      <s-btn slot="action" color="darkred" big @click="$refs.userModal.close()">取消</s-btn>
    </s-modal>
  </div>
</template>

<script>
  import { viewMixin } from '../../mixins'
  import { QTabs, QTab, QTabPane, QList, QItem, QItemMain, QScrollArea, extend } from 'quasar'
  import { email } from 'vuelidate/lib/validators'

  export default {
    name: 'UserManage',
    mixins: [viewMixin],
    mounted () {
      this.userBackup = extend({}, this.user)
      this.groupBackup = { groupId: null, groupName: '', description: '' }
      this.get('getAuthLookupTypePairs', {type: 'UserSourceType'}, r => {
        if (r && r.length > 0) {
          this.sourceTypeOptions = r
          this.user.sourceType = this.userBackup.sourceType = r[0].value
        }
      })
      this.get('getAuthGroupList', {}, r => { this.allGroupList = r })
    },
    data () {
      return {
        search: {
          userAccount: '',
          displayName: ''
        },
        user: {
          userId: null,
          userAccount: '',
          displayName: '',
          sourceType: 'SDB',
          email: '',
          isActive: 'Y'
        },
        userTemp: {},
        userList: [],
        allGroupList: [],
        groupList1: [],
        groupList2: [],
        group1: {},
        group2: {},
        sourceTypeOptions: [],
        isActiveOptions: [
          { label: '生效', value: 'Y' },
          { label: '失效', value: 'N' }
        ],
        columns: [
          { label: '帳號', field: 'userAccount', width: '100px', sort: true },
          { label: '顯示名稱', field: 'displayName', width: '100px', sort: true },
          { label: '帳號來源', field: 'sourceType', width: '100px', sort: true },
          { label: 'Email', field: 'email', width: '150px', sort: true },
          { label: '是否生效',
            field: 'isActive',
            width: '50px',
            sort: true,
            formatter: val => {
              return val && val === 'N' ? '失效' : '生效'
            }},
          { label: '備註', field: 'memo', width: '200px', sort: true }
        ]
      }
    },
    methods: {
      initGroup () {
        if (this.userTemp.userId) {
          this.get('getAuthUserGroup', this.userTemp, r => {
            this.groupList2 = this.allGroupList.filter(g => r.filter(rr => rr.groupId === g.groupId).length === 0)
            this.groupList1 = r
            this.clearGroup()
          })
        }
        else {
          this.clearGroup()
          this.groupList2 = this.allGroupList
          this.groupList1 = []
        }
      },
      selectGroup1 (group) {
        if (this.group1 === group) group = extend({}, this.groupBackup)
        this.group1 = group
      },
      selectGroup2 (group) {
        if (this.group2 === group) group = extend({}, this.groupBackup)
        this.group2 = group
      },
      clearGroup () {
        this.group1 = extend({}, this.groupBackup)
        this.group2 = extend({}, this.groupBackup)
      },
      moveLeft () {
        if (!this.user.userId) return
        if (this.group2.groupId) {
          this.groupList1.push(this.group2)
          this.groupList2 = this.groupList2.filter(g => g.groupName !== this.group2.groupName)
          this.clearGroup()
        }
      },
      moveRight () {
        if (!this.user.userId) return
        if (this.group1.groupId) {
          this.groupList2.push(this.group1)
          this.groupList1 = this.groupList1.filter(g => g.groupName !== this.group1.groupName)
          this.clearGroup()
        }
      },
      moveAllLeft () {
        if (!this.user.userId) return
        this.groupList1 = this.groupList1.concat(this.groupList2)
        this.groupList2 = []
        this.clearGroup()
      },
      moveAllRight () {
        if (!this.user.userId) return
        this.groupList2 = this.groupList2.concat(this.groupList1)
        this.groupList1 = []
        this.clearGroup()
      },
      userChecked () {
        let user = this.$refs.userTable.getChecked()
        this.user = user ? extend({}, user) : extend({}, this.userBackup)
      },
      findUser () {
        this.$refs.pager.search(this.search)
        this.user = extend({}, this.userBackup)
        this.clearGroup()
        this.groupList2 = this.groupList1 = []
      },
      clearSearch () {
        this.search = extend({}, this.userBackup)
        this.$refs.pager.clean()
        this.user = extend({}, this.userBackup)
      },
      addUser () {
        this.userTemp = extend({}, this.userBackup)
        this.initGroup()
        this.$refs.userModal.open()
        this.$refs.tabs.selectTab('tab-user')
      },
      editUser () {
        this.userTemp = extend({}, this.user)
        this.userTemp.isActive = this.userTemp.isActive ? this.userTemp.isActive : 'Y'
        this.initGroup()
        this.$refs.userModal.open()
        this.$refs.tabs.selectTab('tab-user')
      },
      saveUser () {
        if (!this.userTemp.userAccount) this.showError('請輸入帳號')
        else if (!this.userTemp.displayName) this.showError('請輸入顯示名稱')
        else if (!this.userTemp.email) this.showError('請輸入Email')
        else if (!email(this.userTemp.email)) this.showError('Email輸入格式錯誤')
        else {
          this.put('saveAuthUser', this.userTemp, r => {
            this.post('saveAuthUserGroup/' + r.userId, this.groupList1, () => {
              this.$refs.userModal.close()
              this.showSuccess('帳號儲存成功', 3000)
              this.findUser()
            })
          })
        }
      },
      delUser () {
        this.confirm('刪除帳號', '是否確定刪除該帳號？', () => {
          this.delete('deleteAuthUser', {userId: this.user.userId}, () => {
            this.showSuccess('帳號刪除成功', 3000)
            this.user = extend({}, this.userBackup)
            this.findUser()
          })
        })
      }
    },
    components: {
      QTabs, QTab, QTabPane, QList, QItem, QItemMain, QScrollArea
    }
  }
</script>

<style>
  .group-manage .q-card-actions {
    padding-left: 0;
    padding-right: 0;
  }
</style>
