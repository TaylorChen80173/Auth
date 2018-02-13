<template>
  <div class="role-manage" style="padding: 0 10px">
    <div class="row">
      <div class="col-4">
        <q-card>
          <div class="card-header row items-center">
            <q-card-title data-background-color="darkblue"><span style="color: white">角色管理</span></q-card-title>
            <span class="s-card-title col-auto"></span>
          </div>
          <s-input label="過濾條件" v-model="filterItem" style="padding: 5px 10px"></s-input>
          <q-card-actions align="center">
            <div class="s-card-actions" data-background-color="darkblue"></div>
            <s-btn color="darkred" big style="padding: 0 8px" @click="addRole">新增角色</s-btn>
            <s-btn color="darkred" big style="padding: 0 8px" @click="editRole" :disable="!role.roleName">編輯角色</s-btn>
            <s-btn color="darkred" big style="padding: 0 8px" @click="delRole" :disable="!role.roleName">刪除角色</s-btn>
          </q-card-actions>
          <q-card-separator/>
          <q-card-main style="padding-top: 10px">
            <q-scroll-area style="height: 200px">
              <q-list hightlight link separator no-border striped>
                <q-item v-for="(r, index) in roleFilter" :key="index" @click="selectRole(r, $event)">
                  <q-item-main :label="r.roleName" :style="{color: role.roleName === r.roleName ? 'red' : 'black'}"></q-item-main>
                </q-item>
              </q-list>
            </q-scroll-area>
          </q-card-main>
        </q-card>
      </div>
      <div class="col-8" style="padding-left: 10px">
        <q-card>
          <div class="card-header row items-center">
            <q-card-title data-background-color="darkblue"><span style="color: white">應用程式管理</span></q-card-title>
            <span class="s-card-title col-auto"></span>
          </div>
          <q-card-actions align="center">
            <div class="s-card-actions" data-background-color="darkblue"></div>
            <s-btn color="darkred" big style="padding: 0 8px" @click="addAp" :disable="!role.roleName">新增應用程式</s-btn>
            <s-btn color="darkred" big style="padding: 0 8px" @click="delAp" :disable="!mapping.applicationId">刪除應用程式</s-btn>
          </q-card-actions>
          <q-card-separator/>
          <q-card-main style="padding-top: 10px">
            <q-scroll-area style="height: 253px">
              <s-datatable ref="mappingTable" :source="mappingList" striped checkboxs @checked="mappingChecked">
                <s-datatable-column slot="columns" v-for="(column, index) in mappingColumns" :key="index" :column="column"></s-datatable-column>
              </s-datatable>
            </q-scroll-area>
          </q-card-main>
        </q-card>
      </div>
    </div>
    <div class="row justify-between md-gutter">
      <s-card color="darkblue" title="<div class='full-width text-center'>已選功能限制</div>" title-align="full-width" class="col-5">
        <s-btn slot="titleRight" big color="darkred" @click="saveRIMapping">儲存</s-btn>
        <q-scroll-area slot="content" style="height: 200px">
          <q-list hightlight link separator striped>
            <q-item v-for="(a, index) in aItemList1" :key="index">
              <q-item-main>
                <q-item-tile label @click="selectAItem1(a, $event)">
                  <div class="row" :style="{color: aItem1.aItemName === a.aItemName ? 'red' : 'black'}">
                    <div class="col">{{a.aItemName}}</div>
                    <div class="col-auto">{{a.aItemCode}}</div>
                  </div>
                </q-item-tile>
                <q-item-tile v-if="aItem1.aItemName === a.aItemName">
                  <div style="height: 65px">
                    <s-input class="full-width" v-model="aItem1.aItemValue" type="textarea" :min-rows="3" :max-height="60"></s-input>
                  </div>
                </q-item-tile>
              </q-item-main>
            </q-item>
          </q-list>
        </q-scroll-area>
      </s-card>
      <s-card color="darkblue" title="　" class="col">
        <div slot="content" style="height: 200px">
          <div class="row text-center items-center justify-center" style="height: 25%"><s-btn flat icon="fa-angle-right" style="padding-right: 5px" @click="moveRight"></s-btn></div>
          <div class="row text-center items-center justify-center" style="height: 25%"><s-btn flat icon="fa-angle-double-right" style="padding-right: 5px" @click="moveAllRight"></s-btn></div>
          <div class="row text-center items-center justify-center" style="height: 25%"><s-btn flat icon="fa-angle-double-left" style="padding-right: 5px" @click="moveAllLeft"></s-btn></div>
          <div class="row text-center items-center justify-center" style="height: 25%"><s-btn flat icon="fa-angle-left" style="padding-right: 5px" @click="moveLeft"></s-btn></div>
        </div>
      </s-card>
      <s-card color="darkblue"  title="<div class='full-width text-center'>未選功能限制</div>" title-align="full-width" class="col-5">
        <q-scroll-area slot="content" style="height: 200px">
          <q-list hightlight link separator striped>
            <q-item v-for="(a, index) in aItemList2" :key="index" @click="selectAItem2(a, $event)">
              <q-item-main :label="a.aItemName + ' ' + a.aItemCode" :style="{color: aItem2.aItemName === a.aItemName ? 'red' : 'black'}"></q-item-main>
            </q-item>
          </q-list>
        </q-scroll-area>
      </s-card>
    </div>
    <s-modal ref="roleModal" title="角色成員維護" width="800px" height="510px">
      <template slot="content">
        <div class="row justify-center">
          <div class="col-12 row justify-center">
            <s-input class="col-6" label="角色名稱" v-model="roleTemp.roleName" flag style="padding-bottom: 10px"></s-input>
            <s-select class="col-6" label="是否有效" v-model="roleTemp.isValid" :options="isValidOptions"></s-select>
          </div>
          <div class="col-12 row justify-center xs-gutter" style="padding-bottom: 10px">
            <div><s-btn color="darkred" big @click="$refs.memberModal.open()">加入成員</s-btn></div>
            <div><s-btn color="darkred" big @click="delRoleDetail">移除成員</s-btn></div>
            <div><s-btn color="darkred" big @click="saveRole">儲存</s-btn></div>
            <div><s-btn color="darkred" big @click="$refs.roleModal.close()">取消</s-btn></div>
          </div>
        </div>
        <q-scroll-area style="height: 300px">
          <s-datatable ref="roleDetailTable" :source="roleDetailList" striped multiple checkboxs>
            <s-datatable-column slot="columns" v-for="(column, index) in detailColumns" :key="index" :column="column"></s-datatable-column>
          </s-datatable>
        </q-scroll-area>
      </template>
    </s-modal>
    <s-modal ref="mappingModal" title="應用程式維護" width="420px" height="200px">
      <template slot="content">
        <s-select label="應用程式名稱" v-model="mappingTemp.applicationId" :options="apOptions"></s-select>
      </template>
      <s-btn slot="action" color="darkred" big @click="saveAp">儲存</s-btn>
      <s-btn slot="action" color="darkred" big @click="$refs.mappingModal.close()">取消</s-btn>
    </s-modal>
    <common-add-user ref="memberModal" @select="addRoleMember"></common-add-user>
  </div>
</template>

<script>
  import { viewMixin } from '../../mixins'
  import { QCard, QCardTitle, QCardMain, QCardActions, QCardSeparator, QList, QItem, QItemMain, QItemTile, QScrollArea, extend } from 'quasar'
  import CommonAddUser from './common-add-user.vue'

  export default {
    name: 'RoleManage',
    mixins: [viewMixin],
    mounted () {
      this.roleBackup = extend({}, this.role)
      this.mappingBackup = extend({}, this.mapping)
      this.aItemBackup = {
        roleMasterId: null,
        applicationId: null,
        aItemId: null,
        aItemDefaultValue: '',
        aItemValue: ''
      }
      this.getLookup()
      this.findRole()
    },
    data () {
      return {
        search: {
          module: 'SDB'
        },
        role: {
          roleMasterId: null,
          roleName: '',
          isValid: 'Y'
        },
        mapping: {
          applicationId: null,
          roleMasterId: null
        },
        roleDetail: {
          roleMasterId: null,
          roleDetailId: null,
          roleType: 'U',
          roleMemberId: null
        },
        filterItem: '',
        roleTemp: {},
        roleList: [],
        mappingTemp: {},
        mappingList: [],
        roleDetailList: [],
        aItemList1: [],
        aItemList2: [],
        aItem1: {},
        aItem2: {},
        apOptions: [],
        roleTypeOptions: [
          { label: '使用者', value: 'U' },
          { label: '群組', value: 'G' }
        ],
        isValidOptions: [
          { label: '有效', value: 'Y' },
          { label: '失效', value: 'N' }
        ],
        mappingColumns: [
          { label: '模組名稱', field: 'moduleName', width: '100px', sort: true },
          { label: '角色名稱', field: 'roleName', width: '100px', sort: true },
          { label: '應用程式名稱', field: 'applicationName', width: '100px', sort: true }
        ],
        detailColumns: [
          { label: '類別',
            field: 'roleType',
            width: '60px',
            sort: true,
            formatter: val => {
              let type = this.roleTypeOptions.filter(a => a.value === val)
              return type && type.length > 0 ? type[0].label : ''
            }},
          { label: '名稱', field: 'memberName', width: '100px', sort: true }
        ]
      }
    },
    computed: {
      roleFilter () {
        return this.roleList.filter(r => r.roleName.indexOf(this.filterItem) > -1)
      }
    },
    methods: {
      getLookup () {
        this.get('getAuthApplicationPairs', {module: this.search.module}, ap => {
          if (ap && ap.length > 0) {
            ap.forEach(a => { a.value = parseInt(a.value) })
            this.apOptions = ap
            this.mappingBackup.applicationId = ap[0].value
          }
        })
      },
      selectRole (role) {
        if (this.role.roleMasterId === role.roleMasterId) {
          this.role = extend({}, this.roleBackup)
          this.clearMapping()
        }
        else {
          this.role = role
          this.findMapping()
        }
      },
      findMapping () {
        this.clearMapping()
        this.get('getAuthRIMappingList', this.role, r => {
          this.mappingList = r
        })
      },
      selectAItem1 (aItem) {
        if (!this.mapping.applicationId) return
        if (this.aItem1 === aItem) aItem = extend({}, this.groupBackup)
        this.aItem1 = aItem
      },
      selectAItem2 (aItem) {
        if (!this.mapping.applicationId) return
        if (this.aItem2 === aItem) aItem = extend({}, this.groupBackup)
        this.aItem2 = aItem
      },
      clearAItem () {
        this.aItem1 = extend({}, this.aItemBackup)
        this.aItem2 = extend({}, this.aItemBackup)
      },
      moveLeft () {
        if (!this.mapping.applicationId) return
        if (this.aItem2.aItemId) {
          this.aItem2.aItemValue = this.aItem2.aItemDefaultValue
          this.aItemList1.push(this.aItem2)
          this.aItemList2 = this.aItemList2.filter(a => a.aItemId !== this.aItem2.aItemId)
          this.clearAItem()
        }
      },
      moveRight () {
        if (!this.mapping.applicationId) return
        if (this.aItem1.aItemId) {
          this.aItemList2.push(this.aItem1)
          this.aItemList1 = this.aItemList1.filter(a => a.aItemId !== this.aItem1.aItemId)
          this.clearAItem()
        }
      },
      moveAllLeft () {
        if (!this.mapping.applicationId) return
        this.aItemList2.forEach(a => { a.aItemValue = a.aItemDefaultValue })
        this.aItemList1 = this.aItemList1.concat(this.aItemList2)
        this.aItemList2 = []
        this.clearAItem()
      },
      moveAllRight () {
        if (!this.mapping.applicationId) return
        this.aItemList2 = this.aItemList2.concat(this.aItemList1)
        this.aItemList1 = []
        this.clearAItem()
      },
      saveRIMapping () {
        this.post('saveAuthRIMapping/' + this.role.roleMasterId + '/' + this.mapping.applicationId, this.aItemList1, () => {
          this.showSuccess('角色對應之應用程式功能模組變更成功', 3000)
        })
      },
      mappingChecked () {
        let mapping = this.$refs.mappingTable.getChecked()
        if (mapping) {
          this.aItemBackup.roleMasterId = this.role.roleMasterId
          this.aItemBackup.applicationId = mapping.applicationId
          this.get('getAuthApplicationItemList', mapping, ra => {
            this.get('getAuthRIMappingItemList', mapping, rm => {
              this.mapping = mapping
              rm.forEach(r => {
                let ad = ra.filter(a => r.aItemId === a.aItemId)
                if (ad && ad.length > 0) {
                  r.aItemDefaultValue = ad[0].aItemDefaultValue
                }
              })
              ra.forEach(r => { r.roleMasterId = this.role.roleMasterId })
              this.aItemList1 = rm
              this.aItemList2 = ra.filter(a => rm.filter(rr => rr.aItemId === a.aItemId).length === 0)
            })
          })
        }
        else {
          this.aItemBackup.applicationId = null
          this.mapping = extend({}, this.mappingBackup)
          this.aItem1 = this.aItem2 = {}
          this.aItemList2 = this.aItemList1 = []
        }
      },
      findRole () {
        this.get('getAuthRoleList', {}, r => { this.roleList = r })
        this.role = extend({}, this.roleBackup)
        this.clearMapping()
      },
      clearMapping () {
        this.mapping = extend({}, this.mappingBackup)
        this.mappingList = []
        this.aItem1 = this.aItem2 = {}
        this.aItemList2 = this.aItemList1 = []
      },
      addRole () {
        this.roleTemp = extend({}, this.roleBackup)
        this.roleDetailList = []
        this.$refs.roleModal.open()
      },
      editRole () {
        this.roleTemp = extend({}, this.role)
        this.get('getAuthRoleDetailList', this.role, r => {
          this.roleDetailList = r
          this.$refs.roleModal.open()
        })
      },
      saveRole () {
        if (!this.roleTemp.roleName) this.showError('請輸入角色名稱')
        else {
          this.put('saveAuthRole', this.roleTemp, r => {
            this.roleDetailList.forEach(d => {
              d.roleMasterId = r.roleMasterId
            })
            this.post('saveAuthRoleDetail/' + r.roleMasterId, this.roleDetailList, () => {
              this.$refs.roleModal.close()
              this.showSuccess('角色儲存成功', 3000)
              this.findRole()
            })
          })
        }
      },
      delRole () {
        this.confirm('刪除角色', '是否確定刪除該角色？', () => {
          this.delete('deleteAuthRole', {roleMasterId: this.role.roleMasterId}, r => {
            if (r) {
              this.showSuccess('角色刪除成功', 3000)
              this.role = extend({}, this.roleBackup)
              this.findRole()
            }
            else {
              this.showError('尚有成員於角色中，請先行移除', 3000)
            }
          })
        })
      },
      addAp () {
        this.mappingTemp = extend({}, this.mappingBackup)
        this.$refs.mappingModal.open()
      },
      saveAp () {
        this.get('initAuthRIMapping', {roleMasterId: this.role.roleMasterId, applicationId: this.mappingTemp.applicationId}, () => {
          this.$refs.mappingModal.close()
          this.showSuccess('應用程式儲存成功', 3000)
          this.findMapping()
        })
      },
      delAp () {
        this.confirm('刪除應用程式', '是否確定刪除該應用程式？', () => {
          this.delete('deleteAllAuthRIMapping', {roleMasterId: this.role.roleMasterId, applicationId: this.mapping.applicationId}, () => {
            this.showSuccess('應用程式刪除成功', 3000)
            this.mapping = extend({}, this.mappingBackup)
            this.findMapping()
          })
        })
      },
      addRoleMember (members) {
        if (members.length > 0) {
          members = members.filter(u => this.roleDetailList.filter(r => r.roleType === u.roleType && r.roleMemberId === u.roleMemberId).length === 0)
          this.roleDetailList = this.roleDetailList.concat(members)
        }
      },
      delRoleDetail () {
        let ms = this.$refs.roleDetailTable.getChecked()
        if (ms.length > 0) {
          this.roleDetailList = this.roleDetailList.filter(r => ms.filter(m => r.roleType === m.roleType && r.roleMemberId === m.roleMemberId).length === 0)
        }
      }
    },
    components: {
      CommonAddUser, QCard, QCardTitle, QCardMain, QCardActions, QCardSeparator, QList, QItem, QItemMain, QItemTile, QScrollArea
    }
  }
</script>

<style>
  .role-manage .q-card-actions {
    padding-left: 0;
    padding-right: 0;
  }
</style>
