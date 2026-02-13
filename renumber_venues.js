/**
 * سكربت إعادة ترقيم القاعات والفنادق
 * يعيد ترقيم حقل "number" بالتتابع من 1 إلى آخر عنصر
 * الفنادق أولاً ثم القاعات المستقلة - ترقيم متسلسل واحد
 *
 * يعمل على ملفين:
 *  - bahrain_wedding_halls.json
 *  - bahrain_wedding_venues_images_fixed.json
 *
 * @format
 */

const fs = require("fs");
const path = require("path");

const files = [
  path.resolve("app/src/main/res/raw/bahrain_wedding_halls.json"),
  path.resolve(
    "app/src/main/res/raw/HTML-TEST-DATA-PAGE/bahrain_wedding_venues_images_fixed.json",
  ),
];

function renumberFile(filePath) {
  const fileName = path.basename(filePath);
  console.log(`\n📄 Processing: ${fileName}`);

  let raw = fs.readFileSync(filePath, "utf8").replace(/^\uFEFF/, "");
  let data;
  try {
    data = JSON.parse(raw);
  } catch (e) {
    console.error(`  ❌ ERROR parsing JSON: ${e.message}`);
    return;
  }

  let counter = 1;

  // ترقيم الفنادق أولاً
  if (data.hotels && Array.isArray(data.hotels)) {
    data.hotels.forEach((hotel) => {
      const oldNum = hotel.number;
      hotel.number = counter;
      if (oldNum !== counter) {
        console.log(`  🏨 ${hotel.titleEnglish}: ${oldNum} → ${counter}`);
      }
      counter++;
    });
    console.log(
      `  ✅ Hotels renumbered: 1 to ${counter - 1} (${data.hotels.length} hotels)`,
    );
  }

  // ترقيم القاعات المستقلة
  if (data.independentHalls && Array.isArray(data.independentHalls)) {
    data.independentHalls.forEach((hall) => {
      const oldNum = hall.number;
      hall.number = counter;
      if (oldNum !== counter) {
        console.log(`  🏛️  ${hall.titleEnglish}: ${oldNum} → ${counter}`);
      }
      counter++;
    });
    console.log(
      `  ✅ Halls renumbered: ${counter - data.independentHalls.length} to ${counter - 1} (${data.independentHalls.length} halls)`,
    );
  }

  fs.writeFileSync(filePath, JSON.stringify(data, null, 2), "utf8");
  console.log(`  📊 Total items: ${counter - 1}`);
}

files.forEach((filePath) => {
  if (fs.existsSync(filePath)) {
    renumberFile(filePath);
  } else {
    console.log(`⚠️  File not found: ${filePath}`);
  }
});

console.log("\n✨ Done! All venues renumbered sequentially.");
