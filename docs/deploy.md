# デプロイ時の懸念点と対処

## 1. 共通 jar の変更が反映されない
共通 jar は Tomcat の lib/ にイメージとして焼き込んでいます。WAR をコピーしても共通 jar は更新されません。

WAR をコピー → Tomcat が再デプロイ

  ↓

lib/ の共通 jar はそのまま ← 反映されない

対処： 共通 jar を変更した場合はイメージの再ビルドが必要です。

```bash
bashdocker compose up --build -d
```

## 2. Tomcat の設定ファイル変更が反映されない
server.xml や context.xml はボリュームでマウントしていますが、変更してもTomcat の再起動が必要です。WAR のコピーだけでは反映されません。

対処： 設定ファイルを変更した場合はコンテナを再起動します。

```bash
bashdocker compose restart product-a-1
```

## 3. WAR コピー中に Tomcat がデプロイを開始する
WAR が大きい場合、コピーが完了する前に Tomcat がデプロイを開始し、壊れた状態でデプロイされる可能性があります。

対処： 一時ファイルとしてコピーしてからリネームします。

### 直接コピー（リスクあり）
```bash
cp target/app.war /deploy/product-a/app.war
```

### 一時ファイル経由
```bash
cp target/app.war /deploy/product-a/app.war.tmp
mv /deploy/product-a/app.war.tmp /deploy/product-a/app.war
mv はアトミック操作なので Tomcat が中途半端なファイルを掴む心配がありません。
```

## 4. フロントエンドのビルド忘れ
TypeScript・Vite・Tailwind のビルド成果物を WAR に含める場合、Maven のビルド前にフロントエンドのビルドを実行する必要があります。フロントエンドのビルドを忘れると古い資材のままデプロイされます。

対処： pom.xml に frontend-maven-plugin を組み込んでビルドを一本化します。

```xml 
<!-- pom.xml -->
<plugin>
    <groupId>com.github.eirslett</groupId>
    <artifactId>frontend-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals><goal>pnpm</goal></goals>
            <configuration>
                <arguments>run build</arguments>
            </configuration>
        </execution>
    </executions>
</plugin>
```

これで mvn package 一発でフロントエンドのビルドも含まれます。