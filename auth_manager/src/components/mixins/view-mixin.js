import { Alert, SessionStorage, extend } from 'quasar'

export default {
  inject: ['app'],
  mounted () {
    if (this.$options.name === 'login' || typeof this.$options.name === 'undefined') {
      this.app.shrink()
    }
    else {
      this.app.expand()
    }
  },
  data () {
    return {
    }
  },
  computed: {
    baseURL () {
      return this.$http.api.defaults.baseURL
    }
  },
  methods: {
    get (name, model, callback) {
      return this.$http.api
        .get(name, {
          params: model
        })
        .then(response => {
          if (callback) {
            callback(response.data)
          }
        })
    },
    async asyncGet (name, model) {
      const response = await this.$http.api.get(name, { params: model })
      return response.data
    },
    post (name, model, callback) {
      return this.$http.api
        .post(name, model)
        .then(response => {
          if (callback) {
            callback(response.data)
          }
        })
    },
    put (name, model, callback) {
      return this.$http.api
        .put(name, model)
        .then(response => {
          if (callback) {
            callback(response.data)
          }
        })
    },
    delete (name, model, callback) {
      return this.$http.api
        .delete(name, {
          params: model
        })
        .then(response => {
          if (callback) {
            callback(response.data)
          }
        })
    },
    download (name, model, callback) {
      return this.$http.download
        .get(name, {
          params: model
        })
        .then(callback)
    },
    showSuccess (text, duration) {
      const alert = Alert.create({
        color: 'green',
        icon: 'fa-check-circle',
        html: text,
        enter: 'bounceInLeft',
        leave: 'bounceOutRight'})
      if (duration) {
        setTimeout(() => { alert.dismiss() }, duration)
      }
    },
    showError (text) {
      this.app.openDialog({
        title: '提示訊息',
        message: text,
        icon: 'fa-exclamation',
        color: '#3399ff',
        btnColor: 'blue',
        cancelText: '確定'
      })
    },
    showWarning (text) {
      this.app.openDialog({
        title: '提示訊息',
        message: text,
        icon: 'fa-exclamation',
        color: '#3399ff',
        btnColor: 'blue',
        cancelText: '確定'
      })
    },
    confirm (title, text, callback) {
      this.app.openDialog({
        title: title,
        message: text,
        icon: 'fa-question',
        color: 'orange',
        submitText: '確認',
        cancelText: '取消',
        submit: callback
      })
    },
    message (title, text, btnName, callback) {
      this.app.openDialog({
        title: title,
        message: text,
        icon: 'fa-exclamation',
        color: '#3399ff',
        btnColor: 'blue',
        submitText: btnName,
        submit: callback || this.noCallbackReturn
      })
    },
    noCallbackReturn () {
      return false
    },
    route (name, params, search, pager) {
      SessionStorage.remove('search')
      SessionStorage.remove('pager')
      if (search) {
        SessionStorage.set('search', search)
      }
      if (pager) {
        let p = this.$refs[pager]
        SessionStorage.set('pager',
          { ref: pager, pageSize: p.innerPageSize, pageNumber: p.index, loaded: p.loaded })
      }
      this.$router.push(params ? { name: name, params: params } : { name: name })
    },
    preMount (loaded, search) {
      if (SessionStorage.has('search')) {
        let localSearch = SessionStorage.get.item('search')
        if (localSearch) {
          let obj = extend(true, {}, localSearch)
          if (search) {
            this.assign(search, obj)
          }
          else {
            this.search = obj
          }
          SessionStorage.remove('search')
        }
      }
      if (SessionStorage.has('pager')) {
        let localPager = SessionStorage.get.item('pager')
        if (localPager) {
          let pager = extend(true, {}, localPager)
          let p = this.$refs[pager.ref]
          p.index = pager.pageNumber
          p.innerPageSize = pager.pageSize
          loaded = pager.loaded
        }
        SessionStorage.remove('pager')
      }
      if (loaded) {
        this.find()
      }
    },
    back () {
      this.$router.go(-1)
    },
    assign (target, source) {
      Object.keys(source).forEach(key => {
        if (!source[key] || typeof source[key] !== 'object') {
          target[key] = source[key]
        }
        else if (source[key] instanceof Date) {
          target[key] = new Date()
          target[key].setTime(source[key].getTime())
        }
        else if (source[key] instanceof Array) {
          target[key] = []
          for (let i = 0, len = source[key].length; i < len; i++) {
            target[key][i] = source[key][i]
          }
        }
        else if (source[key] instanceof Object) {
          this.assign(target[key], source[key])
        }
      })
    },
    find () {
    }
  }
}
