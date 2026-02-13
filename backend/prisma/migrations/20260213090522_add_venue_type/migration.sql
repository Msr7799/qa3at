-- CreateEnum
CREATE TYPE "VenueType" AS ENUM ('HOTEL', 'HALL', 'RESORT');

-- AlterTable
ALTER TABLE "venues" ADD COLUMN     "type" "VenueType" NOT NULL DEFAULT 'HOTEL';
