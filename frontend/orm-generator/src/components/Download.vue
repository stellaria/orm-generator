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
          this.downloadRes(res, this.$store.state.package+'.zip')
        });
    },
    downloadRes(data, name) {
      if (!data) return;
      const fileName = name;
      let url = window.URL.createObjectURL(new Blob([data]));
      let link = document.createElement('a');
      link.style.display = 'none';
      link.href = url;
      link.setAttribute('download', fileName);
      document.body.appendChild(link);
      link.click();
    },
  },
};
</script>

<style>
</style>