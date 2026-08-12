# 在 GitHub Actions 上打包本项目 APK 的说明

## 之前打包为什么会失败

分析了项目里的配置文件（`build.gradle.kts`、`app/build.gradle.kts`、
`gradle/libs.versions.toml`、`.gitignore` 等），发现了几个必然导致 CI 打包失败的问题：

1. **仓库里没有 Gradle Wrapper**（`gradlew` / `gradlew.bat` /
   `gradle/wrapper/gradle-wrapper.jar`）。这是这个项目压缩包本身缺失的部分——
   多数 GitHub 教程默认执行 `./gradlew assembleDebug`，但这个命令在这个仓库里
   根本不存在，会直接报 `gradlew: No such file or directory`。

2. **`debug.keystore` 被 `.gitignore` 排除**，但 `app/build.gradle.kts` 里
   debug 构建类型的签名配置又指向这个文件（`${rootDir}/debug.keystore`）。
   仓库里没有这个文件，CI 上一构建就会在签名阶段报错。

3. **release 签名配置依赖环境变量和本地文件**
   （`KEYSTORE_PATH` / `STORE_PASSWORD` / `KEY_PASSWORD` /
   `my-upload-key.jks`），这些在 CI 环境里默认都不存在。

4. **项目使用的是很新的工具链**：AGP 9.1.1 + compileSdk 36（ext 1）+
   Kotlin 2.2.10，要求 **JDK 17 起步、Gradle 9.1.0**。如果 CI 没有显式配置
   JDK 版本和 Gradle 版本，很容易因为工具链不匹配而失败。

5. Android SDK 的许可协议（licenses）在全新的 CI 环境里默认没有被接受，
   下载 `platforms;android-36`、`build-tools;36.0.0` 时会失败。

6. 项目引入了 Firebase（`firebase-ai`、`firebase-appcheck-recaptcha`），
   正常情况下 `google-services` 插件在找不到 `google-services.json` 时会
   直接报错终止构建——好在这个项目已经在 `app/build.gradle.kts` 里配置了
   `missingGoogleServicesStrategy = WARN`，所以**这一点不用你额外处理**，
   没有 `google-services.json` 也能正常打包（只是运行时 Firebase 相关功能会不可用）。

## 我做了什么

我在项目里新增了两个 GitHub Actions workflow 文件，直接解决以上第 1–5 点问题：

- `.github/workflows/build-apk.yml`
  —— 推送到 `main`/`master` 分支或手动触发时，自动构建 **debug APK**
  （不需要任何签名密钥，适合日常测试安装）。
- `.github/workflows/build-release-apk.yml`
  —— 手动触发，构建**正式签名的 release APK**（需要你自己提供签名密钥，见下）。

这两个 workflow 做的事：
- 用 `actions/setup-java` 配置 JDK 17
- 用 `android-actions/setup-android` 配置 Android SDK 并自动接受许可协议
- 用 `gradle/actions/setup-gradle` 直接提供 Gradle 9.1.0（不依赖仓库里没有的
  gradlew，所以哪怕你不生成 wrapper 也能正常跑）
- CI 里现场生成一个临时 `debug.keystore` 用于签名 debug 包
- 构建完成后把 APK 作为 Artifact 上传，可在 Actions 运行记录页面直接下载

## 使用步骤

1. 把整个项目（包含新增的 `.github/workflows/` 文件夹）推送到你的 GitHub 仓库。
2. 打开仓库的 **Actions** 标签页，应该能看到 `Build Debug APK` 这个 workflow。
   推送代码会自动触发一次；你也可以点击 **Run workflow** 手动触发。
3. 等构建完成（一般几分钟），在这次运行的页面底部 **Artifacts** 区域下载
   `warmjournal-debug-apk`，解压后就是可以直接安装到手机上的 `.apk` 文件。

### 如果你需要「正式签名」的 APK（比如要上架应用商店）

1. 本地生成一个正式签名用的 keystore（只需要做一次，一定要妥善保管，丢了就没法更新应用）：
   ```
   keytool -genkeypair -v -keystore my-upload-key.jks -alias upload \
     -keyalg RSA -keysize 2048 -validity 10000
   ```
2. 把这个文件转成 base64 文本：
   ```
   base64 -i my-upload-key.jks | tr -d '\n' > keystore_base64.txt
   ```
3. 打开 GitHub 仓库 → **Settings → Secrets and variables → Actions**，
   新增三个 Repository secret：
   - `KEYSTORE_BASE64`：粘贴 `keystore_base64.txt` 里的全部内容
   - `STORE_PASSWORD`：你设置的 keystore 密码
   - `KEY_PASSWORD`：你设置的 key 密码
4. 回到 Actions 页面，手动运行 `Build Release APK` 这个 workflow，
   完成后在 Artifacts 里下载 `warmjournal-release-apk`。

## 建议（可选，长期更省心）

在电脑上用 Android Studio 打开一次这个项目，Sync 完成后它会自动帮你生成
`gradlew` / `gradlew.bat` / `gradle/wrapper/gradle-wrapper.jar` 这几个
wrapper 文件。把它们一起提交到仓库后，以后无论本地还是 CI 都可以直接用
`./gradlew assembleDebug` 命令，行为更贴近 Android 官方推荐的标准做法
（我现在给的 workflow 不依赖它，所以没有也完全没问题，只是加上更规范）。
