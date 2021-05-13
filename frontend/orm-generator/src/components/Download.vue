<template>
  <div>
    <img src="@/assets/zip.png" alt="" />
    <p>{{ this.$store.state.package + ".zip" }}</p>
    <el-button type="primary" plain @click="handleDownload">下载</el-button>
  </div>
</template>

<script>
import * as api from "@/api/api.js";
export default {
  data() {},
  methods: {
    handleDownload() {
      api
        .downloadRequest({ package: this.$store.state.package })
        .then((res) => {
					console.log('aaaaaaaa,aaaaaaaa')
          this.downloadRes(res, this.$store.state.package+'.zip')
        });
    },
    downloadRes(data, name) {
      if (!data) return;
      const content = data;
      const blob = new Blob([content]);
      const fileName = name;
      if ("download" in document.createElement("a")) {
        // 非IE下载
        const elink = document.createElement("a");
        elink.download = fileName;
        elink.style.display = "none";
        elink.href = URL.createObjectURL(blob);
        document.body.appendChild(elink);
        elink.click();
        URL.revokeObjectURL(elink.href); // 释放URL 对象
        document.body.removeChild(elink);
      } else {
        // IE10+下载
        navigator.msSaveBlob(blob, fileName);
      }
    },
  },
};
</script>

<style>
</style>