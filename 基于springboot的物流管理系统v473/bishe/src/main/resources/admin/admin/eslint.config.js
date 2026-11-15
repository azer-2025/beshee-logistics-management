import pluginVue from 'eslint-plugin-vue'
import vueTsEslintConfig from '@vue/eslint-config-typescript'
import skipFormatting from '@vue/eslint-config-prettier/skip-formatting'

export default [
  {
    name: 'app/files-to-lint',
    files: ['**/*.{ts,mts,tsx,vue}'],
  },

  {
    name: 'app/files-to-ignore',
    ignores: ['**/dist/**', '**/dist-ssr/**', '**/coverage/**'],
  },

  ...pluginVue.configs['flat/essential'],
  ...vueTsEslintConfig(),
  skipFormatting,
  // 👇 新增规则覆盖配置（必须放在最后）
  {
    files: ['**/*.{ts,mts,tsx,vue}'],
    rules: {
      "@typescript-eslint/no-unused-vars": "off" // 关闭未使用变量检查
    }},
  {
    rules: {
      'no-console': 'off',
      'vue/multi-word-component-names':'off',
    }
  }

]
