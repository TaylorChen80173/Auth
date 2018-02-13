<template>
  <q-field :style="fieldStyle"
    :icon="icon"
    :helper="helper"
    :error="invalidAndDirty"
    :error-label="errorLabel"
    :count="count"
    :dark="dark"
    :label-width="labelWidth"
    :inset="inset"
    label=""
  >
    <div v-if="label && label.length > 0" class="col row items-center justify-end" slot="label">
      <q-icon v-if="needFlag | flag" name="fa-star" color="red" class="col-auto" style="padding-top: 4px"></q-icon>
      <span class="col-auto">{{label}}</span>
    </div>
    <q-input class="col-auto"
      v-model="model"
      :type="actualType"
      :clearable="clearable"
      :no-pass-toggle="noPassToggle"
      :min-rows="minRows"
      :max-height="maxHeight"
      :min="min"
      :max="max"
      :step="step"
      :max-decimals="maxDecimals"
      :autofocus="autofocus"
      :placeholder="actualPlaceholder"
      :name="name"
      :max-length="maxLength"
      :loading="loading"
      :prefix="prefix"
      :suffix="suffix"
      :stack-label="stackLabel"
      :float-label="floatLabel"
      :inverted="inverted"
      :dark="dark"
      :error="invalidAndDirty"
      :disable="actualDisable"
      :color="color"
      :align="align"
      :before="before"
      :after="after"
      @change="change"
      @click="type.toLowerCase() === 'file' ? __pick() : $emit('click')"
      @focus="$emit('focus')"
      @blur="$emit('blur')"
      @keydown="keydown"
      @keyup="$emit('keyup')"
      ref="input"
    ><input v-if="(type.toLowerCase() === 'file') && disable === false"
      type="file"
      ref="file"
      class="q-uploader-input absolute-full cursor-pointer"
      :accept="extensions"
      v-bind.prop="{multiple: multiple}"
      @change="__add"
    ></q-input>
  </q-field>
</template>

<script>
  import { QField, QInput, QIcon, format } from 'quasar'
  import { viewMixin } from '../mixins'
  const { humanStorageSize } = format

  export default {
    mixins: [viewMixin],
    props: {
      value: { required: true },
      // QField
      label: String,
      icon: String,
      helper: String,
      count: {
        type: [Number, Boolean],
        default: false
      },
      labelWidth: {
        type: Number,
        default: 3,
        validator (val) {
          return val >= 1 && val < 12
        }
      },
      inset: {
        type: String,
        validator (val) {
          return ['icon', 'label', 'full'].includes(val)
        }
      },
      /*
      fieldStyle: {
        type: Object,
        default: {} // { paddingRight: '10px' }
      },
      */
      fieldStyle: {
        type: [Object, Array],
        default: null // { paddingRight: '10px' }
      },
      // QInput
      type: {
        type: String,
        default: 'text'
      },
      clearable: Boolean,
      noPassToggle: Boolean,
      // for TextArea
      minRows: Number,
      maxHeight: Number,
      // for Type number
      min: Number,
      max: Number,
      step: {
        type: Number,
        default: 1
      },
      maxDecimals: Number,
      // common field properties
      autofocus: Boolean,
      placeholder: String,
      name: String,
      maxLength: Number,
      loading: Boolean,
      // common frame properties
      prefix: String,
      suffix: String,
      stackLabel: String,
      floatLabel: String,
      inverted: Boolean,
      dark: Boolean,
      disable: Boolean,
      color: String,
      align: {
        type: String,
        default: 'left',
        validator: v => ['left', 'center', 'right'].includes(v)
      },
      before: {
        type: Array,
        validator: v => v.every(i => 'icon' in i)
      },
      after: {
        type: Array,
        validator: v => v.every(i => 'icon' in i)
      },
      // Validate
      v: Object,
      vError: Object,
      noFlag: {
        type: Boolean,
        default: false
      },
      flag: {
        type: Boolean,
        default: false
      },
      // File
      filename: {
        type: String,
        default: 'file'
      },
      multiple: {
        type: Boolean,
        default: false
      },
      extensions: String,
      url: {
        type: String
      },
      urlFactory: {
        type: Function
      },
      additionalFields: Object,
      method: {
        type: String,
        default: 'POST'
      }
    },
    data () {
      return {
        queue: [],
        files: [],
        uploading: false,
        uploadedSize: 0,
        totalSize: 0,
        xhrs: []
      }
    },
    computed: {
      fileLength () {
        return this.queue.length
      },
      actualType () {
        return this.type.toLowerCase() === 'file' ? 'text' : this.type
      },
      actualDisable () {
        return this.type.toLowerCase() === 'file' ? true : this.disable
      },
      actualPlaceholder () {
        return this.placeholder ? this.placeholder : (this.displayName ? this.displayName
          : (this.type.toLowerCase() === 'file' ? '請點擊選擇檔案'
            : (this.label ? '請輸入' + this.label : '')))
      },
      needFlag () {
        return !this.noFlag && this.v && this.v.hasOwnProperty('required')
      },
      model: {
        get () {
          return this.value
        },
        set (val) {
          if (val !== this.value) {
            if (this.type.toLowerCase() === 'number') {
              if (val === null) {
                val = ''
              }
              else {
                val = Number.isInteger(this.maxDecimals)
                  ? parseFloat(val).toFixed(this.maxDecimals)
                  : parseFloat(val)
              }
            }
            this.$emit('input', val)
            this.$emit('change', val)
          }
        }
      },
      errorLabel () {
        let error = ''
        if (this.vError && this.invalidAndDirty) {
          error = Object.keys(this.vError).filter(x => {
            return !this.v[x] && this.v.$dirty
          }).shift()
        }
        return typeof error !== 'undefined' && error !== '' ? this.vError[error] : ''
      },
      invalidAndDirty () {
        return this.v && this.v.$error
      }
    },
    methods: {
      blur () {
        this.$refs.input.blur()
      },
      focus () {
        this.$refs.input.focus()
      },
      select () {
        this.$refs.input.select()
      },
      clear () {
        if (this.type.toLowerCase() === 'file') {
          this.reset()
        }
        this.$refs.input.clear()
      },
      change (value) {
        if (this.v) {
          var req = this.v.hasOwnProperty('required')
          if (value === '' && !req) {
            this.v.$reset()
          }
          else {
            this.v.$touch()
          }
        }
        this.$emit('change', value)
      },
      keydown (e) {
        if (this.type.toLowerCase() === 'number') {
          if (e.key === '+' || e.key === 'e' || e.key === 'E') {
            e.preventDefault()
          }
        }
        this.$emit('keydown', e)
      },
      __add (e) {
        this.__reset()
        let files = Array.prototype.slice.call(e.target.files)
        this.$refs.file.value = ''
        let flag = ''
        files = files.filter(file => !this.queue.some(f => file.name === f.name))
          .map(file => {
            initFile(file)
            file.__size = humanStorageSize(file.size)
            if (this.noThumbnails || !file.type.startsWith('image')) {
              flag = 'file'
              file.__type = file.type
              this.queue.push(file)
            }
            else {
              const reader = new FileReader()
              reader.onload = (e) => {
                flag = 'img'
                let img = new Image()
                img.src = e.target.result
                file.__img = img
                file.__type = file.type
                this.queue.push(file)
                if (flag === 'img') {
                  this.$emit('added', this.queue)
                }
              }
              reader.readAsDataURL(file)
            }
            return file
          })
        this.files = files
        this.$emit('add', files)
        this.model = files.map(f => f.name).join(',')
        if (flag === 'file') {
          this.$emit('added', this.queue)
        }
      },
      __pick () {
        this.model = ''
        if (this.$q.platform.is.mozilla) {
          this.$refs.file.click()
        }
      },
      __getUploadPromise (file) {
        const
          form = new FormData(),
          xhr = new XMLHttpRequest()
        try {
          form.append('Content-Type', file.type || 'application/octet-stream')
          form.append('Authorization', this.$http.api.defaults.headers.common['Authorization'])
          if (file instanceof Array) {
            file.forEach(f => form.append(this.filename + '[]', f))
          }
          else {
            form.append(this.filename, file)
          }
          if (this.additionalFields) {
            Object.keys(this.additionalFields).forEach(key => {
              form.append(key, this.additionalFields[key])
            })
          }
        }
        catch (e) {
          return
        }
        initFile(file)
        file.xhr = xhr
        return new Promise((resolve, reject) => {
          xhr.onreadystatechange = () => {
            if (xhr.readyState < 4) {
              return
            }
            if (xhr.status && xhr.status < 400) {
              file.__doneUploading = true
              file.__progress = 100
              this.$emit('uploaded', file, xhr)
              resolve(xhr, file)
            }
            else {
              file.__failed = true
              this.$emit('fail', file, xhr)
              reject(xhr)
            }
          }
          xhr.onerror = () => {
            file.__failed = true
            this.$emit('fail', file, xhr)
            reject(xhr)
          }
          const resolver = this.urlFactory
            ? this.urlFactory(file)
            : Promise.resolve(this.url)
          resolver.then(url => {
            xhr.open(this.method, url, true)
            if (this.headers) {
              Object.keys(this.headers).forEach(key => {
                xhr.setRequestHeader(key, this.headers[key])
              })
            }
            this.xhrs.push(xhr)
            xhr.send(form)
          })
        })
      },
      fileUpload () {
        const length = this.fileLength
        if (length === 0) {
          return
        }
        this.uploadedSize = 0
        this.uploading = true
        this.xhrs = []
        this.$emit('start')
        let solved = (xhr) => {
          this.uploading = false
          this.xhrs = []
          var resp = {}
          try {
            resp = xhr.responseText ? JSON.parse(xhr.responseText) : {}
          }
          catch (e) {}
          if (xhr.status && xhr.status < 400) {
            this.$emit('finish', resp, xhr)
          }
          else {
            // Toast.create.negative({
            //   html: resp.message && resp.message.length > 0 ? resp.message : 'ERROR',
            //   timeout: 5000
            // })
            this.showError(resp.message && resp.message.length > 0 ? resp.message : 'ERROR')
          }
        }
        if (length === 1) {
          this.__getUploadPromise(this.queue[0]).then(solved).catch(solved)
        }
        else {
          this.__getUploadPromise(this.queue).then(solved).catch(solved)
        }
      },
      abort () {
        this.xhrs.forEach(xhr => { xhr.abort() })
      },
      __reset () {
        this.abort()
        this.files = []
        this.queue = []
        this.__computeTotalSize()
      },
      __computeTotalSize () {
        this.totalSize = this.length
          ? this.queue.map(f => f.size).reduce((total, size) => total + size)
          : 0
      },
      reset () {
        this.__reset()
        this.$emit('reset')
      },
      getImageBase64 () {
        return this.queue.map(file => file.__img ? file.__img.src : '')
      }
    },
    components: {
      QField, QInput, QIcon
    }
  }

  function initFile (file) {
    file.__doneUploading = false
    file.__failed = false
    file.__uploaded = 0
    file.__progress = 0
  }
</script>

<style>
</style>
