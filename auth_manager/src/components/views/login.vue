<template>
  <div v-if="true" class="row justify-center items-center fullscreen">
    <div class="login-container">
      <div class="login-content">
        <div class="text-center" style="margin-bottom: 30px"><img src="../../assets/image/advtek_logo.png"></div>
        <div class="text-center" style="margin-bottom: 40px; padding-left: 80px;font-size: 40px; font-family: DFKai-sb; color:white" >權限管理系統平台</div>
        <div style="width: 500px; margin: 0 auto">
          <s-input label="帳號" :label-width="2" v-model="model.username" :v="$v.model.username" :v-error="error.username" autofocus no-flag @blur="onUserBlur"></s-input>
          <s-input label="密碼" :label-width="2" v-model="model.password" type="password" :v="$v.model.password" :v-error="error.password" no-flag no-pass-toggle></s-input>
          <s-select label="身分" :label-width="2" v-model="model.roleCode" :options="roleList"></s-select>
          <div class="row justify-center items-center">
            <div class="col">
              <s-input label="　　" :label-width="3" placeholder="請輸入圖片中之文字" v-model="model.captcha" no-flag></s-input>
              <div v-if="captchaError" class="q-field-bottom row no-wrap" style="padding-top: 0">
                <div class="col-3"></div>
                <div class="q-field-error col">{{captchaError}}</div>
              </div>
            </div>
            <img class="col-auto" style="width:90px;height:40px;padding: 0 5px" :src="captchaImage">
            <a class="col-auto text-yellow" @click="getCaptcha">重新產生</a>
          </div>
          <div class="row justify-end items-center" style="padding-top: 10px">
            <s-btn class="col-5" style="border-radius: 8px; font-size: 20px; font-weight: 700; width: 100%; background-color: lightblue" big @click="submit">LOGIN</s-btn>
            <div class="float-right text-yellow" style="padding-left: 10px">忘記密碼？</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { required, minLength, email } from 'vuelidate/lib/validators'
  import { viewMixin } from '../mixins'
  import { mapGetters, mapMutations } from 'vuex'
  import { layout } from '../../configs/constants'
  import { LocalStorage, SessionStorage, QInput, QSelect } from 'quasar'
  import { checkPassword } from '../views/validate'

  export default {
    name: 'login',
    mixins: [viewMixin],
    created () {
      this.setLoginFirst(true)
      let token = LocalStorage.get.item('jhi-authenticationtoken')
      if (token) {
        this.storeToken(token)
      }
    },
    mounted () {
      this.getCaptcha()
    },
    data () {
      return {
        roleList: [{ label: '無身分選擇', value: null }],
        captchaImage: '',
        disable: false, // TODO: using recaptcha must set to true
        model: {
          username: 'am@bs.com',
          password: '',
          rememberMe: true,
          roleCode: null,
          captcha: ''
        },
        error: {
          username: { required: '請輸入帳號!!', email: '請輸入正確 E-Mail' },
          password: { required: '請輸入使用者密碼', minLength: '請輸入至少五位文數字', checkPwd: '密碼必須包括數字與英文字符' }
        },
        captchaError: ''
      }
    },
    validations: {
      model: {
        username: { required, email },
        password: { required, minLength: minLength(3), checkPwd: (value) => { return checkPassword(value) } }
      }
    },
    computed: {
      ...mapGetters(['tokenInfo']),
      title () {
        return layout.titleChiText + '登入作業'
      }
    },
    methods: {
      ...mapMutations(['setAuthToken', 'setAccountInfo', 'setLoginFirst']),
      submit () {
        this.route('StudentMaintenance')
        this.$v.model.$touch()
        if (this.model.captcha === '') {
          this.captchaError = '請輸入驗證碼!!'
        }
        else {
          if (this.model.captcha.length === 4) {
            this.get('login/validateCaptcha', {captcha: this.model.captcha}, r => {
              if (r === '') {
                this.captchaError = ''
                // if (!this.$v.model.$error && this.model.roleCode) {
                if (!this.$v.model.$error) {
                  this.post('login/authenticate', this.model, rr => {
                    this.storeToken(rr.id_token)
                  })
                }
              }
              else {
                this.captchaError = '驗證碼錯誤，請重新輸入!!'
                r = r.replace('data:application/json;charset=utf-8;base64,', '')
                this.captchaImage = 'data:image/png;base64,' + r
              }
            })
          }
          else {
            this.captchaError = '驗證碼錯誤，請重新輸入!!'
          }
        }
      },
      validateUser () {
        this.$v.model.$touch()
        if (!this.$v.model.$error && this.model.roleCode) {
          this.post('login/authenticate', this.model, r => {
            this.storeToken(r.id_token)
          })
        }
      },
      storeToken (token) {
        this.setAuthToken(token)
        this.$http.api.defaults.headers.common['Authorization'] = 'Bearer ' + token
        // for swagger authentication
        if (this.model.rememberMe) {
          LocalStorage.set('jhi-authenticationtoken', token)
        }
        else {
          SessionStorage.set('jhi-authenticationtoken', token)
        }
        this.getAccountInfo()
      },
      getAccountInfo () {
        this.get('login/account', {}, r => {
          this.setAccountInfo(r)
          this.route('UserManage')
        })
      },
      getCaptcha (response) {
        this.get('login/getCaptcha', {}, r => {
          r = r.replace('data:application/json;charset=utf-8;base64,', '')
          this.captchaImage = 'data:image/png;base64,' + r
        })
      },
      onUserBlur () {
        let name = this.model.username
        if (name) {
          this.get('login/roleList', { username: name }, r => {
            this.roleList = (!r || r.length === 0) ? [{ label: '無身分選擇', value: null }] : r
            this.model.roleCode = this.roleList[0].value
          })
        }
      }
    },
    components: {
      QInput, QSelect
    }
  }
</script>

<style>
  .login-content {
    position: relative;
    z-index: 2;
    font-size: 20px;
  }​
  .login-container {
    position: relative;
  }
  .login-container:before {
    content: "";
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    z-index: -1;
    /* background-image: url('../../assets/image/login-bg.png'); */
    background-color: #213649;
  }
  .login-content .q-field-label-inner span {
    text-align: center;
    color: white;
    font-size: 20px;
    font-weight: 700;
  }
  .login-content .q-field-error {
    color: #ffd8da;
  }
</style>
