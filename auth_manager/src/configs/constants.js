// 畫面 LAYOUT 規範
export const layout = {
  titleChiText: '權限管理系統平台',
  titleEngText: 'Authority Management',
  view: 'hHh Lpr fFf',
  reveal: false,
  leftScroll: true,
  rightScroll: true,
  leftBreakpoint: 996,
  rightBreakpoint: 0,
  hideTabs: false,
  headerColor: '#213649',
  footerColor: 'grey-4'
}

// 網路相關設定
export const network = {
  baseAddress: 'http://60.251.22.61:8080/AuthManagerAPI/'
  // baseAddress: 'http://localhost:8080/'
}

export const tableOptions = {
  compileTemplates: true,
  highlightMatches: true,
  pagination: {
    dropdown: true,
    chunk: 5
  },
  perPage: 5,
  filterByColumn: false,
  texts: {
    filter: '　查　詢：'
  },
  datepickerOptions: {
    showDropdowns: true
  }
}

export const menu = [
  { name: '權限管理',
    icon: 'fa-user-secret',
    show: true,
    menu: [
          { name: '帳號管理', route: '/Authority/UserManage', alias: 'UserManage', path: 'views/authority/user-manage' },
          { name: '群組管理', route: '/Authority/GroupManage', alias: 'GroupManage', path: 'views/authority/group-manage' },
          { name: '應用程式管理', route: '/Authority/ApplicationManage', alias: 'ApplicationManage', path: 'views/authority/application-manage' },
          { name: '角色管理', route: '/Authority/RoleManage', alias: 'RoleManage', path: 'views/authority/role-manage' },
          { name: '選單管理', route: '/Authority/MenuManage', alias: 'MenuManage', path: 'views/authority/menu-manage' }
    ]
  }
]
