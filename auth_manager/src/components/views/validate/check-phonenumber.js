var phoneUtil = require('google-libphonenumber').PhoneNumberUtil.getInstance()

export default (value) => {
  if (!value) {
    return true
  }
  try {
    return phoneUtil.isValidNumber(phoneUtil.parse(value, 'TW'))
  }
  catch (e) {
    return false
  }
}
