<template>
  <div class="group-manage row" style="padding: 0 10px">
    <q-card class="col-4">
      <div class="card-header row items-center">
        <q-card-title data-background-color="darkblue"><span style="color: white">群組管理</span></q-card-title>
        <span class="s-card-title col-auto"></span>
      </div>
      <s-input label="過濾條件" v-model="filterItem" style="padding: 5px 10px"></s-input>
      <q-card-actions align="center">
        <div class="s-card-actions" data-background-color="darkblue"></div>
        <s-btn color="darkred" big style="padding: 0 8px" @click="addGroup">新增群組</s-btn>
        <s-btn color="darkred" big style="padding: 0 8px" @click="editGroup" :disable="!group.groupName">編輯群組</s-btn>
        <s-btn color="darkred" big style="padding: 0 8px" @click="delGroup" :disable="!group.groupName">刪除群組</s-btn>
      </q-card-actions>
      <q-card-separator/>
      <q-card-main style="padding-top: 10px">
        <q-scroll-area style="height: calc(100vh - 300px)">
          <q-list hightlight link separator no-border striped>
            <q-item v-for="(g, index) in groupFilter" :key="index" @click="selectGroup(g, $event)">
              <q-item-main :label="g.groupName" :style="{color: group.groupName === g.groupName ? 'red' : 'black'}"></q-item-main>
            </q-item>
          </q-list>
        </q-scroll-area>
      </q-card-main>
    </q-card>
    <s-card class="col-8" color="darkblue" title="群組成員管理" style="padding-left: 10px">
      <div slot="content">
        <div class="row items-center" style="padding-bottom: 10px">
          <span class="col-auto">選擇群組：{{group.groupName}}</span>
          <div class="col row justify-end xs-gutter">
            <div class="col-auto"><s-btn color="darkred" @click="$refs.userModal.open()">加入成員</s-btn></div>
            <div class="col-auto"><s-btn color="darkred" @click="removeUser()">移除成員</s-btn></div>
          </div>
        </div>
        <q-scroll-area style="height: calc(100vh - 235px)">
          <s-datatable ref="userTable" :source="userList" striped multiple checkboxs>
            <s-datatable-column slot="columns" v-for="(column, index) in columns" :key="index" :column="column"></s-datatable-column>
          </s-datatable>
        </q-scroll-area>
      </div>
    </s-card>
    <s-modal ref="groupModal" title="群組維護" width="400px" height="290px">
        <s-input slot="content" label="群組名稱" v-model="groupTemp.groupName"></s-input>
        <s-input slot="content" label="群組說明" v-model="groupTemp.description" type="textarea" :min-rows="5" :max-height="100"></s-input>
        <s-btn slot="action" color="darkred" big @click="saveGroup">儲存</s-btn>
        <s-btn slot="action" color="darkred" big @click="$refs.groupModal.close()">取消</s-btn>
    </s-modal>
    <common-add-user ref="userModal" tabs="user" @select="addUser"></common-add-user>
  </div>
</template>

<script>
  import { viewMixin } from '../../mixins'
  import { QCard, QCardTitle, QCardMain, QCardActions, QCardSeparator, QList, QItem, QItemMain, QScrollArea, extend } from 'quasar'
  import CommonAddUser from './common-add-user.vue'

  export default {
    name: 'GroupManage',
    mixins: [viewMixin],
    mounted () {
      this.groupBackup = extend({}, this.group)
      this.findGroup()
    },
    data () {
      return {
        group: {
          groupId: null,
          groupName: '',
          description: ''
        },
        groupBackup: {},
        groupTemp: {},
        filterItem: '',
        groupName: '',
        userList: [],
        groupList: [],
        columns: [
          { label: '群組成員', field: 'userAccount', width: '100px', sort: true },
          { label: '顯示名稱', field: 'displayName', width: '100px', sort: true },
          { label: 'Email', field: 'email', width: '150px', sort: true }
        ]
      }
    },
    computed: {
      groupFilter () {
        return this.groupList.filter(g => g.groupName.indexOf(this.filterItem) > -1)
      }
    },
    methods: {
      findGroup () {
        this.get('getAuthGroupList', {}, r => {
          this.groupList = r
        })
      },
      findUser () {
        this.get('getAuthGroupUserList', this.group, r => {
          this.userList = r
        })
      },
      selectGroup (group) {
        if (group.groupId !== this.group.groupId) {
          this.group = group
          this.findUser()
        }
        else {
          this.group = extend({}, this.groupBackup)
          this.userList = []
        }
      },
      addGroup () {
        this.groupTemp = extend({}, this.groupBackup)
        this.$refs.groupModal.open()
      },
      editGroup () {
        this.groupTemp = extend({}, this.group)
        this.$refs.groupModal.open()
      },
      saveGroup () {
        if (!this.groupTemp.groupName) {
          this.showError('請輸入群組名稱')
          return
        }
        this.put('saveAuthGroup', this.groupTemp, () => {
          this.$refs.groupModal.close()
          this.showSuccess('群組儲存成功', 3000)
          this.findGroup()
        })
      },
      delGroup () {
        this.confirm('刪除群組', '是否確定刪除該群組？', () => {
          this.delete('deleteAuthGroup', {groupId: this.group.groupId}, r => {
            if (r) {
              this.showSuccess('群組刪除成功', 3000)
              this.group = extend({}, this.groupBackup)
              this.findGroup()
            }
            else {
              this.showError('群組尚有角色關聯無法刪除')
            }
          })
        })
      },
      addUser (users) {
        if (users.length > 0) {
          users = users.map(u => { return {groupId: this.group.groupId, userId: u.roleMemberId} })
          users = users.filter(u => this.userList.filter(r => r.userId === u.userId).length === 0)
          if (users.length > 0) {
            this.userList = this.userList.concat(users)
            this.put('saveAuthGroupDetails', users, () => {
              this.showSuccess('群組成員加入成功', 3000)
              this.findUser()
            })
          }
        }
      },
      removeUser () {
        let users = this.$refs.userTable.getChecked()
        if (users.length > 0) {
          console.log(users)
          this.confirm('移除成員', '是否確定移除群組成員？', () => {
            this.post('deleteAuthGroupDetails', users, () => {
              this.showSuccess('群組成員移除成功', 3000)
              this.findUser()
            })
          })
        }
      }
    },
    components: {
      CommonAddUser, QCard, QCardTitle, QCardMain, QCardActions, QCardSeparator, QList, QItem, QItemMain, QScrollArea
    }
  }
</script>

<style>
  .group-manage .q-card-actions {
    padding-left: 0;
    padding-right: 0;
  }
</style>
