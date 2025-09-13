<template>
  <div class="page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">商品列表</h2>
    </div>

    <!-- 筛选和搜索区域 -->
    <div class="filter-bar">
      <!-- 分类筛选 -->
      <div class="category-filter">
        <el-select
            v-model="selectedCategory"
            placeholder="全部分类"
            clearable
            @change="handleFilterChange"
            class="category-select"
        >
          <el-option label="全部分类" value=""></el-option>
          <el-option
              v-for="category in categoryList"
              :key="category"
              :label="category"
              :value="category"
          ></el-option>
        </el-select>
      </div>

      <!-- 搜索框 -->
      <div class="search-box">
        <el-input
            v-model="searchKeyword"
            placeholder="搜索商品名称或描述"
            clearable
            class="search-input"
            @keyup.enter.native="handleSearch"
        >
          <el-button icon="el-icon-search" @click="handleSearch"></el-button>
        </el-input>
      </div>
    </div>

    <!-- 商品网格布局（直接循环 products 数组，筛选后的数据存在这里） -->
    <div class="product-list-container">
      <div
          class="product-card"
          v-for="item in products"
          :key="item.id"
          @click="goDetail(item.id)"
      >
        <!-- 商品封面图区域 -->
        <div class="product-cover-wrapper">
          <img
              v-if="item.cover"
              :src="item.cover"
              :alt="item.name"
              class="product-cover"
              loading="lazy"
          />
          <div class="cover-placeholder" v-else>
            <i class="el-icon-picture-outline"></i>
            <span>暂无图片</span>
          </div>
        </div>

        <!-- 商品信息区域 -->
        <div class="product-info">
          <h3 class="product-name">{{ item.name }}</h3>
          <p class="product-desc" v-if="item.introduce">{{ item.introduce }}</p>
          <p class="product-desc placeholder-desc" v-else>暂无商品简介</p>

          <!-- 显示商品分类 -->
          <div class="product-category">
            <span class="category-label">分类：</span>
            <span class="category-value">{{ item.category }}</span>
          </div>

          <div class="product-meta">
            <span class="product-price">¥ {{ item.price }}</span>
            <span class="product-stock">库存：{{ item.stock }} 件</span>
          </div>
        </div>

        <!-- 详情按钮 -->
        <div class="product-action">
          <el-button size="mini" type="primary" plain @click.stop="goDetail(item.id)">
            查看详情
          </el-button>
        </div>
      </div>
    </div>

    <!-- 空列表提示（判断 products 是否为空） -->
    <div class="empty-tip" v-if="products.length === 0">
      <i class="el-icon-search"></i>
      <p>没有找到符合条件的商品</p>
    </div>
    <CartFab />
  </div>
</template>

<script>
import { getProductList, getCategoryList } from "@/api/product";
import CartFab from "@/components/CartFab.vue";

export default {
  name: "ProductList",
  components: { CartFab },
  data() {
    return {
      products: [],          // 筛选后要渲染的商品数组（核心数据源）
      categoryList: [],      // 分类列表（用于下拉选项）
      selectedCategory: "",  // 选中的分类（筛选条件）
      searchKeyword: ""      // 搜索关键词（筛选条件）
    };
  },
  async created() {
    // 页面初始化：先加载分类，再加载商品（确保分类下拉能正常显示）
    await this.fetchCategoryList();
    await this.fetchProductList();
  },
  methods: {
    /**
     * 请求分类列表（单独抽离，避免代码冗余）
     */
    async fetchCategoryList() {
      try {
        const categoryResponse = await getCategoryList();
        // 确保返回的是数组，否则设为空数组
        this.categoryList = Array.isArray(categoryResponse.data)
            ? categoryResponse.data
            : [];
      } catch (error) {
        console.error("分类列表请求失败：", error);
        this.categoryList = [];
        this.$message.error("分类加载失败，请稍后重试");
      }
    },

    /**
     * 请求商品列表（核心：带筛选条件，后续可扩展分页参数）
     * 注：若后端需要分页参数，后续可在参数中添加 currentPage、pageSize
     */
    async fetchProductList() {
      try {
        // 传筛选条件给后端：分类、搜索关键词（根据后端接口参数顺序调整）
        const productResponse = await getProductList(
            null,
            this.selectedCategory,  // 第一个参数：分类（按你后端接口定义调整）
            this.searchKeyword      // 第二个参数：搜索关键词（按你后端接口定义调整）
            // 后续加分页：可追加参数，如 this.currentPage, this.pageSize
        );

        // 确保返回的是数组，否则设为空数组（避免渲染报错）
        this.products = Array.isArray(productResponse.data)
            ? productResponse.data
            : [];

      } catch (error) {
        console.error("商品列表请求失败：", error);
        this.products = [];  // 失败时清空数组，显示空提示
        this.$message.error("商品加载失败，请稍后重试");
      }
    },

    /**
     * 跳转到商品详情页
     */
    goDetail(id) {
      this.$router.push({ path: `/product/${id}` });
    },

    /**
     * 分类变化时触发：重新请求商品列表
     */
    handleFilterChange() {
      // 重新请求商品（此时会带上最新的 selectedCategory）
      this.fetchProductList();
      // 滚动到顶部（优化用户体验）
      window.scrollTo(0, 0);
    },

    /**
     * 搜索触发：重新请求商品列表（回车/点击搜索按钮）
     */
    handleSearch() {
      // 重新请求商品（此时会带上最新的 searchKeyword）
      this.fetchProductList();
      // 滚动到顶部（优化用户体验）
      window.scrollTo(0, 0);
    }
  }
};
</script>

<style scoped>
/* 页面基础样式 */
.page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

/* 页面标题 */
.page-header {
  margin-bottom: 24px;
}
.page-title {
  font-size: 20px;
  color: #1f3b57;
  margin: 0;
  font-weight: 600;
}

/* 筛选和搜索区域 */
.filter-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  flex-wrap: wrap; /* 适配小屏幕：换行显示 */
  align-items: center;
}

/* 分类筛选样式 */
.category-filter {
  flex: 0 0 auto;
}
.category-select {
  width: 200px;
}

/* 搜索框样式 */
.search-box {
  flex: 1;
  min-width: 280px; /* 确保小屏幕上有足够宽度 */
}
.search-input {
  width: 100%;
  max-width: 500px;
}

/* 商品列表容器（网格布局） */
.product-list-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
}

/* 单个商品卡片 */
.product-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  border: 1px solid #f0f5ff;
}
/* 卡片 hover 效果 */
.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
  border-color: #e6f4ff;
}

/* 商品封面图容器 */
.product-cover-wrapper {
  width: 100%;
  height: 200px;
  background-color: #f9fbfd;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
/* 商品图片 */
.product-cover {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}
/* 图片 hover 放大 */
.product-card:hover .product-cover {
  transform: scale(1.03);
}
/* 图片占位符（无图时显示） */
.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #8aa8c2;
}
.cover-placeholder i {
  font-size: 32px;
  margin-bottom: 8px;
}
.cover-placeholder span {
  font-size: 14px;
}

/* 商品信息区域 */
.product-info {
  padding: 16px;
}
/* 商品名称（最多两行，超出省略） */
.product-name {
  font-size: 16px;
  color: #1f3b57;
  margin: 0 0 8px 0;
  font-weight: 500;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.4;
}
/* 商品简介（单行省略） */
.product-desc {
  font-size: 14px;
  color: #6b87a1;
  margin: 0 0 8px 0;
  line-height: 1.5;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
/* 无简介时的占位样式 */
.placeholder-desc {
  color: #b0c4d8;
  font-style: italic;
}

/* 商品分类标签 */
.product-category {
  font-size: 12px;
  color: #8aa8c2;
  margin-bottom: 10px;
  padding: 4px 0;
  border-bottom: 1px dashed #f0f5ff;
}
.category-label {
  color: #b0c4d8;
}
.category-value {
  background-color: #f0f7ff;
  padding: 2px 6px;
  border-radius: 4px;
}

/* 商品价格+库存区域 */
.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}
.product-price {
  color: #1677ff;
  font-weight: 600;
  font-size: 15px;
}
.product-stock {
  color: #8aa8c2;
}

/* 详情按钮（hover 时显示） */
.product-action {
  position: absolute;
  bottom: 16px;
  right: 16px;
  opacity: 0;
  transition: opacity 0.3s ease;
}
.product-card:hover .product-action {
  opacity: 1;
}

/* 空列表提示 */
.empty-tip {
  margin: 80px auto;
  text-align: center;
  color: #8aa8c2;
}
.empty-tip i {
  font-size: 48px;
  margin-bottom: 16px;
  display: inline-block;
}
.empty-tip p {
  font-size: 16px;
  margin: 0;
}

/* 响应式适配（小屏幕调整） */
@media (max-width: 992px) {
  .product-list-container {
    grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  }
}
@media (max-width: 576px) {
  .product-list-container {
    grid-template-columns: 1fr; /* 手机端：单列显示 */
  }
  .product-cover-wrapper {
    height: 180px; /* 手机端：缩小图片高度 */
  }
  .page {
    padding: 12px; /* 手机端：减少内边距 */
  }
  .category-select {
    width: 100%; /* 手机端：分类下拉占满宽度 */
  }
}
</style>