export default (value) => {
  if (!value) {
    return false
  }
  // 密碼長度必須大於八位
  if (value.length >= 8) {
    // 密碼必須包括數字與英文字符
    return /^([0-9]+[a-zA-Z]+|[a-zA-Z]+[0-9]+)[0-9a-zA-Z]*$/.test(value)
  }
  else {
    return false
  }
}
