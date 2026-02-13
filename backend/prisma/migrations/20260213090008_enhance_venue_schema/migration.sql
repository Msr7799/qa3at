-- AlterTable
ALTER TABLE "packages" ADD COLUMN     "venueId" TEXT,
ALTER COLUMN "nameAr" DROP NOT NULL,
ALTER COLUMN "description" DROP NOT NULL,
ALTER COLUMN "descriptionAr" DROP NOT NULL,
ALTER COLUMN "tier" DROP NOT NULL,
ALTER COLUMN "category" DROP NOT NULL,
ALTER COLUMN "basePrice" DROP NOT NULL;

-- AlterTable
ALTER TABLE "venues" ADD COLUMN     "accessibility" JSONB,
ADD COLUMN     "contacts" JSONB,
ADD COLUMN     "parking" JSONB,
ADD COLUMN     "pricingModel" TEXT,
ADD COLUMN     "subVenues" JSONB;

-- AddForeignKey
ALTER TABLE "packages" ADD CONSTRAINT "packages_venueId_fkey" FOREIGN KEY ("venueId") REFERENCES "venues"("id") ON DELETE CASCADE ON UPDATE CASCADE;
