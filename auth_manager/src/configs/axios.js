import axios from 'axios'
import { Loading } from 'quasar'
import { network } from './constants'

let app

let loadFunction = config => {
  Loading.show({ message: '處理中，請稍待片刻' })
  return config
}
let finishFunction = response => {
  Loading.hide()
  return response
}
let downloadFinishFunction = response => {
  Loading.hide()
  download(response)
}
let errorFunction = error => {
  Loading.hide()
  let message
  if (error.response) {
    // The request was made and the server responded with a status code
    // that falls out of the range of 2xx
    console.log('ResponseErrorData:', error.response.data)
    console.log('ResponseErrorStatus:', error.response.status)
    console.log('ResponseErrorHeaders:', error.response.headers)
    let data = error.response.data
    if (typeof data.type !== 'undefined' && data.type === 'application/json') {
      blobReader.readAsText(data)
    }
    else {
      message = data.message ? data.message
      : (data.error ? data.error
        : (data.description ? data.description
          : data.AuthenticationException))
    }
  }
  else if (error.request) {
    // The request was made but no response was received
    // `error.request` is an instance of XMLHttpRequest in the browser and an instance of
    // http.ClientRequest in node.js
    console.log('RequestError:', error.request)
    message = error.message
  }
  else {
    // Something happened in setting up the request that triggered an Error
    console.log('ErrorMessage:', error.message)
    message = error.message
  }
  console.log(message)
  app.openDialog({
    title: '提示訊息',
    message: message,
    icon: 'fa-exclamation',
    color: '#3399ff',
    btnColor: 'blue',
    cancelText: '確定'
  })
  /*
  Toast.create.negative({
    html: message,
    timeout: 10000
  })
  */
  return Promise.reject(error)
}

let blobReader = new FileReader()
// This fires after the blob has been read/loaded.
blobReader.addEventListener('loadend', (e) => {
  const text = e.srcElement.result
  var data = JSON.parse(text)
  console.log(data)
  if (data && !data.isSuccess) {
    app.openDialog({
      title: '提示訊息',
      message: data.message,
      icon: 'fa-exclamation',
      color: '#3399ff',
      btnColor: 'blue',
      cancelText: '確定'
    })
    /*
    Toast.create.negative({
      html: data.errorMessage,
      timeout: 10000
    })
    */
  }
})

let download = response => {
  // console.log(JSON.stringify(response))
  let filename = ''
  let suggestedFileName = response.headers['x-suggested-filename']
  if (suggestedFileName) {
    filename = decodeURI(suggestedFileName).replace(/\+/g, ' ')
  }
  if (!filename) {
    if (response.data.type === 'application/json') {
      blobReader.readAsText(response.data)
    }
    else {
      app.openDialog({
        title: '提示訊息',
        message: '無資料可匯出',
        icon: 'fa-exclamation',
        color: '#3399ff',
        btnColor: 'blue',
        cancelText: '確定'
      })
      /*
      Toast.create.negative({
        html: '無資料可匯出',
        timeout: 10000
      })
      */
    }
    return
  }
  let blob = response.data

  if (typeof window.navigator.msSaveBlob !== 'undefined') {
    // IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created. These URLs will no longer resolve as the data backing the URL has been freed."
    window.navigator.msSaveBlob(blob, filename)
  }
  else {
    let URL = window.URL || window.webkitURL
    let downloadUrl = URL.createObjectURL(blob)

    if (filename) {
      // use HTML5 a[download] attribute to specify filename
      let a = document.createElement('a')
      // safari doesn't support this yet
      if (typeof a.download === 'undefined') {
        window.location = downloadUrl
      }
      else {
        a.href = downloadUrl
        a.download = filename
        document.body.appendChild(a)
        a.click()
      }
    }
    else {
      window.location = downloadUrl
    }
    // cleanup
    setTimeout(() => { URL.revokeObjectURL(downloadUrl) }, 100)
  }
}

const axiosInstanceApi = axios.create({ baseURL: network.baseAddress + 'api/' })
// const axiosInstanceApi = axios.create({ baseURL: 'api/' })
axiosInstanceApi.interceptors.request.use(loadFunction)
axiosInstanceApi.interceptors.response.use(finishFunction, errorFunction)

const axiosInstanceDownload = axios.create({ baseURL: network.baseAddress + 'api/', responseType: 'blob' })
// const axiosInstanceDownload = axios.create({ baseURL: 'api/', responseType: 'blob' })
axiosInstanceDownload.interceptors.request.use(loadFunction)
axiosInstanceDownload.interceptors.response.use(downloadFinishFunction, errorFunction)

let clients = {
  $http: {
    get () {
      return {
        app: val => {
          app = val
        },
        api: axiosInstanceApi,
        download: axiosInstanceDownload
      }
    }
  }
}

export default (Vue) => {
  Object.defineProperties(Vue.prototype, clients)
}
