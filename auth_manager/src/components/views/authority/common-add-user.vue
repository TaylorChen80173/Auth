<template>
  <s-modal class="member" ref="memberModal" title="加入成員" width="800px" height="510px">
    <template slot="content">
      <q-tabs ref="tabs" color="deepblue" align="center">
        <q-tab default slot="title" name="tab-user" label="使用者" @select="selectUser" :disable="tabs !== 'all' && tabs !== 'user'"/>
        <q-tab slot="title" name="tab-group" label="群組" @select="selectGroup" :disable="tabs !== 'all' && tabs !== 'group'"/>
        <q-tab-pane name="tab-user">
          <div class="full-width row md-gutter justify-center" style="padding-bottom: 10px">
            <s-input label="帳號" class="col-5" v-model="searchUser.userAccount"></s-input>
            <s-input label="顯示名稱" class="col-5" v-model="searchUser.displayName"></s-input>
          </div>
          <div class="row justify-center xs-gutter" style="padding-bottom: 10px">
            <div><s-btn color="darkred" big @click="$refs.userPager.search(searchUser)">查詢</s-btn></div>
            <div><s-btn color="darkred" big @click="addUserMember">選擇</s-btn></div>
            <div><s-btn color="darkred" big @click="$refs.memberModal.close()">取消</s-btn></div>
          </div>
          <s-paginator ref="userPager" url="getAuthUserList" :page-size="5" style="padding-bottom: 20px">
            <template scope="page">
              <s-datatable ref="userTable" :source="page.data" :offset="page.offset" striped multiple checkboxs>
                <s-datatable-column slot="columns" v-for="(column, index) in userColumns" :key="index" :column="column"></s-datatable-column>
              </s-datatable>
            </template>
          </s-paginator>
        </q-tab-pane>
        <q-tab-pane name="tab-group">
          <div class="full-width row md-gutter justify-center" style="padding-bottom: 10px">
            <s-input label="群組名稱" class="col-5" v-model="searchGroup.groupName"></s-input>
          </div>
          <div class="row justify-center xs-gutter" style="padding-bottom: 10px">
            <div><s-btn color="darkred" big @click="$refs.groupPager.search(searchGroup)">查詢</s-btn></div>
            <div><s-btn color="darkred" big @click="addGroupMember">選擇</s-btn></div>
            <div><s-btn color="darkred" big @click="$refs.memberModal.close()">取消</s-btn></div>
          </div>
          <s-paginator ref="groupPager" url="getAuthGroupPageList" :page-size="5" style="padding-bottom: 20px">
            <template scope="page">
              <s-datatable ref="groupTable" :source="page.data" :offset="page.offset" striped multiple checkboxs>
                <s-datatable-column slot="columns" v-for="(column, index) in groupColumns" :key="index" :column="column"></s-datatable-column>
              </s-datatable>
            </template>
          </s-paginator>
        </q-tab-pane>
      </q-tabs>
    </template>
  </s-modal>
</template>

<script>
  import { viewMixin } from '../../mixins'
  import { QTabs, QTab, QTabPane, QScrollArea, extend } from 'quasar'

  export default {
    name: 'CommonAddUser',
    mixins: [viewMixin],
    mounted () {
      this.searchUserBackup = extend({}, this.searchUser)
      this.searchGroupBackup = extend({}, this.searchGroup)
    },
    props: {
      tabs: {
        type: String,
        default: 'all'
      }
    },
    data () {
      return {
        searchUser: {
          userAccount: '',
          displayName: ''
        },
        searchGroup: {
          groupName: ''
        },
        userColumns: [
          { label: '帳號', field: 'userAccount', width: '100px', sort: true },
          { label: '顯示名稱', field: 'displayName', width: '100px', sort: true }
        ],
        groupColumns: [
          { label: '群組名稱', field: 'groupName', width: '100px', sort: true },
          { label: '備註', field: 'description', width: '100px', sort: true }
        ]
      }
    },
    methods: {
      open () {
        this.$refs.memberModal.open()
        this.$refs.tabs.selectTab('tab-user')
      },
      close () {
        this.$refs.memberModal.close()
      },
      selectUser () {
        this.searchUser = extend({}, this.searchUserBackup)
        setTimeout(() => {
          this.$refs.userPager.clean()
        })
      },
      selectGroup () {
        this.searchGroup = extend({}, this.searchGroupBackup)
        setTimeout(() => {
          this.$refs.groupPager.clean()
        })
      },
      addUserMember () {
        let users = this.$refs.userTable.getChecked()
        users = users.map(u => { return {roleType: 'U', roleMemberId: u.userId, memberName: u.displayName} })
        this.$emit('select', users)
        this.close()
      },
      addGroupMember () {
        let groups = this.$refs.groupTable.getChecked()
        groups = groups.map(g => { return {roleType: 'G', roleMemberId: g.groupId, memberName: g.groupName} })
        this.$emit('select', groups)
        this.close()
      }
    },
    components: {
      QTabs, QTab, QTabPane, QScrollArea
    }
  }
</script>

<style>
  .member .layout-padding {
    padding: 10px 10px;
  }
</style>
