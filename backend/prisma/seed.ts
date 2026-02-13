import { PrismaClient, PackageTier, PackageCategory, AddonCategory, PriceType, VenueType } from '@prisma/client';
import * as bcrypt from 'bcryptjs';
import * as fs from 'fs';
import * as path from 'path';

const prisma = new PrismaClient();

// Enhanced types matching the JSON structure
type VenueJson = {
  hotels: BahrainHall[];
  independentHalls: BahrainHall[];
};

type BahrainHall = {
  nid?: string;
  number?: number;
  titleEnglish?: string;
  titleArabic?: string;
  descriptionEN?: string | null;
  city?: string | null;
  mainImage?: string | null;
  imageUrls?: string[] | null;

  // Enhanced fields
  pricingModel?: string;
  pricing?: {
    currency?: string;
    perPersonFrom?: number | null;
    perPersonTo?: number | null;
    weekdayPerPerson?: number | null;
    weekendPerPerson?: number | null;
    isNet?: boolean | null;
    taxes?: any;
  };
  capacitySummary?: {
    indoor?: { min?: number; max?: number; raw?: string };
    outdoor?: { min?: number; max?: number; raw?: string };
  };
  venues?: any[]; // Sub-venues
  packages?: any[]; // Venue-specific packages
  contacts?: any;
  parking?: any;
  accessibility?: any;

  // Old fields for backward compatibility if mixed
  url?: string | null;
  price?: string | number | null;
  maxGuestsIndoor?: string | null;
};

type BahrainCity = {
  city: string;
  lat?: string;
  lng?: string;
};

const CITY_AR_MAP: Record<string, string> = {
  Manama: 'المنامة',
  Muharraq: 'المحرق',
  'Al Muḩarraq': 'المحرق',
  'Madīnat Ḩamad': 'مدينة حمد',
  'Madīnat ‘Īsá': 'مدينة عيسى',
  'Jidd Ḩafş': 'جد حفص',
  'Ar Rifā‘': 'الرفاع',
  Riffa: 'الرفاع',
  Sitra: 'سترة',
  Sitrah: 'سترة',
  'Bani Jamra': 'بني جمرة',
  Bahrain: 'البحرين',
  Zallaq: 'الزلاق',
  'Amwaj Islands': 'جزر أمواج',
  Budaiya: 'البديع',
  Juffair: 'الجفير',
  Seef: 'السيف',
  'Isa Town': 'مدينة عيسى',
  'Hamad Town': 'مدينة حمد',
  Aali: 'عالي',
  Tubli: 'توبلي',
  'Sanabis': 'السنابس',
  'Adliya': 'العدلية',
  'Gudaibiya': 'القضيبية',
  'Hoora': 'الحورة',
  'Janabiyah': 'الجنبية',
  'Saar': 'سار',
  'Mahooz': 'الماحوز',
  'Umm Al Hassam': 'أم الحصم',
  'Busaiteen': 'البسيتين',
  'Hidd': 'الحد',
  'Galali': 'قلالي',
  'Diar Al Muharraq': 'ديار المحرق',
};

function normalizeCity(city: string | null | undefined): string {
  const c = (city || '').trim();
  if (!c || c.toLowerCase() === 'bahrain') return 'Manama';
  if (c === 'Sitrah') return 'Sitra';
  return c;
}

function cityAr(city: string): string {
  return CITY_AR_MAP[city] || city;
}

function parseIntSafe(value: any): number | null {
  if (typeof value === 'number') return Math.trunc(value);
  if (!value) return null;
  const num = parseInt(String(value).replace(/,/g, '').trim(), 10);
  return Number.isFinite(num) ? num : null;
}

function parseFloatSafe(value: any): number | null {
  if (typeof value === 'number') return value;
  if (!value) return null;
  const num = parseFloat(String(value).trim());
  return Number.isFinite(num) ? num : null;
}

function loadBahrainHalls(): BahrainHall[] {
  const filePath = path.join(__dirname, 'bahrain_wedding_venues.json');
  const raw = fs.readFileSync(filePath, 'utf8');
  const data = JSON.parse(raw) as VenueJson;

  // Flatten hotels and halls
  const hotels = (data.hotels || []).map(h => ({ ...h, type: 'HOTEL' }));
  const halls = (data.independentHalls || []).map(h => ({ ...h, type: 'HALL' }));

  return [...hotels, ...halls];
}

function loadBahrainCities(): BahrainCity[] {
  const filePath = path.join(__dirname, 'bh.json');
  try {
    const raw = fs.readFileSync(filePath, 'utf8');
    const data = JSON.parse(raw);
    return (data.BahrainCities || []) as BahrainCity[];
  } catch (e) {
    console.warn('Could not load bh.json, using fallback cities');
    return [];
  }
}

async function main() {
  console.log('Seeding database...');

  // Create admin & vendor (same as before)
  const adminPassword = await bcrypt.hash('admin123', 10);
  await prisma.user.upsert({
    where: { email: 'admin@qa3at.com' },
    update: {},
    create: {
      email: 'admin@qa3at.com',
      password: adminPassword,
      name: 'Admin User',
      role: 'ADMIN',
    },
  });

  const vendor = await prisma.vendor.upsert({
    where: { email: 'vendor@qa3at.com' },
    update: {},
    create: {
      email: 'vendor@qa3at.com',
      name: 'Premium Venues Bahrain',
      nameAr: 'شركة القاعات المميزة البحرين',
      phone: '+97339876543',
      description: 'Leading wedding venue provider in Kingdom of Bahrain',
      descriptionAr: 'الشركة الرائدة في توفير قاعات الأفراح في مملكة البحرين',
    },
  });

  // Cleanup
  console.log('Cleaning up old data...');
  // Delete in correct order to avoid foreign key constraints
  await prisma.packageItem.deleteMany();
  await prisma.package.deleteMany();
  await prisma.venuePhoto.deleteMany();
  await prisma.venueAvailability.deleteMany();
  await prisma.review.deleteMany();
  await prisma.bookingItem.deleteMany();
  await prisma.payment.deleteMany();
  await prisma.booking.deleteMany();
  await prisma.venue.deleteMany(); // Now safe to delete venues

  // Create time slots
  const timeSlots = await Promise.all([
    prisma.timeSlot.upsert({
      where: { id: 'morning' },
      update: {},
      create: { id: 'morning', name: 'Morning', nameAr: 'صباحي', startTime: '09:00', endTime: '14:00' },
    }),
    prisma.timeSlot.upsert({
      where: { id: 'afternoon' },
      update: {},
      create: { id: 'afternoon', name: 'Afternoon', nameAr: 'مسائي', startTime: '14:00', endTime: '19:00' },
    }),
    prisma.timeSlot.upsert({
      where: { id: 'evening' },
      update: {},
      create: { id: 'evening', name: 'Evening', nameAr: 'ليلي', startTime: '19:00', endTime: '00:00' },
    }),
  ]);

  // Load and Process Venues
  const halls = loadBahrainHalls();
  const cities = loadBahrainCities();
  const cityCoords = new Map<string, { lat: number; lng: number }>();
  for (const c of cities) {
    const name = normalizeCity((c.city || '').trim());
    const lat = parseFloatSafe(c.lat);
    const lng = parseFloatSafe(c.lng);
    if (name && lat !== null && lng !== null) {
      cityCoords.set(name, { lat, lng });
    }
  }

  console.log(`Processing ${halls.length} venues...`);

  for (const hall of halls) {
    const cityName = normalizeCity(hall.city);
    const coords = cityCoords.get(cityName);

    // Determine capacity
    let capacity = 0;
    if (hall.capacitySummary?.indoor?.max) {
      capacity = hall.capacitySummary.indoor.max;
    } else if (hall.maxGuestsIndoor) {
      capacity = parseIntSafe(hall.maxGuestsIndoor) || 0;
    } else {
      capacity = 300; // Default fallback
    }

    // Determine price
    let basePrice = 1000;
    let pricePerPerson = 0;
    const pricing = hall.pricing;
    if (pricing) {
      if (pricing.perPersonFrom) pricePerPerson = pricing.perPersonFrom;
      // Estimate base price if only per-person is available
      if (pricePerPerson > 0) basePrice = 0;
    }

    // Photos
    const photos = (hall.imageUrls || []).filter(url => url && !url.includes('weserv.nl')); // Prefer direct links if possible, or use them as is
    if (photos.length === 0 && hall.mainImage) photos.push(hall.mainImage);
    // If still empty or all are weserv proxies that might expire, keep them. 
    // Actually weserv is a proxy, so it's fine. 
    // Let's ensure we have valid URLs.

    const finalPhotos = (hall.imageUrls && hall.imageUrls.length > 0)
      ? hall.imageUrls
      : (hall.mainImage ? [hall.mainImage] : []);

    const nameEn = hall.titleEnglish || hall.titleArabic || 'Unknown Venue';
    const nameAr = hall.titleArabic || hall.titleEnglish || 'قاعة غير معروفة';

    const createdVenue = await prisma.venue.create({
      data: {
        name: nameEn,
        nameAr: nameAr,
        description: hall.descriptionEN || `Wedding venue in ${cityName}`,
        descriptionAr: `قاعة أفراح في ${cityAr(cityName)}`,
        address: cityName,
        addressAr: cityAr(cityName),
        city: cityName,
        cityAr: cityAr(cityName),
        latitude: coords?.lat ?? null,
        longitude: coords?.lng ?? null,
        capacity,
        minCapacity: Math.min(50, capacity),
        pricePerPerson,
        basePrice,
        rating: 4.5, // Default rating
        reviewCount: 0,
        amenities: ['Parking', 'AC', 'Lighting'], // Placeholder
        vendorId: vendor.id,
        type: (hall as any).type === 'HOTEL' ? VenueType.HOTEL : VenueType.HALL,
        pricingModel: hall.pricingModel || 'quote',
        subVenues: hall.venues ? JSON.stringify(hall.venues) : undefined,
        contacts: hall.contacts ? JSON.stringify(hall.contacts) : undefined,
        parking: hall.parking ? JSON.stringify(hall.parking) : undefined,
        accessibility: hall.accessibility ? JSON.stringify(hall.accessibility) : undefined,

        photos: {
          create: finalPhotos.map((url, idx) => ({
            url,
            isPrimary: idx === 0,
            sortOrder: idx
          }))
        }
      }
    });

    // Create Packages for this Venue
    if (hall.packages && Array.isArray(hall.packages)) {
      for (const pkg of hall.packages) {
        // Map package tier intuitively
        let tier: PackageTier = PackageTier.SILVER;
        const nameLower = pkg.name?.toLowerCase() || '';
        if (nameLower.includes('gold') || nameLower.includes('premium')) tier = PackageTier.GOLD;
        if (nameLower.includes('diamond') || nameLower.includes('royal') || nameLower.includes('platinum')) tier = PackageTier.DIAMOND;

        // Remove currency from price if string
        let pkgPrice = 0;
        if (typeof pkg.perPerson === 'number') pkgPrice = pkg.perPerson;

        await prisma.package.create({
          data: {
            name: pkg.name || 'Standard Package',
            nameAr: pkg.name || 'باقة قياسية', // TODO: Translating names would be better
            description: pkg.notes || 'Wedding package',
            descriptionAr: pkg.notes || 'باقة زفاف',
            tier,
            category: PackageCategory.VENUE,
            basePrice: 0,
            pricePerPerson: pkgPrice,
            venueId: createdVenue.id,
            isActive: true,
          }
        });
      }
    }
  }

  // Create some Generic Global Packages (Decoration, etc.)
  const globalPackages = [
    { name: 'Silver Decoration', nameAr: 'ديكور فضي', description: 'Enhanced stage with flowers', descriptionAr: 'كوشة محسنة مع ورود', tier: PackageTier.SILVER, category: PackageCategory.DECORATION, basePrice: 1200, pricePerPerson: 0 },
    { name: 'Gold Decoration', nameAr: 'ديكور ذهبي', description: 'Premium stage and hall decoration', descriptionAr: 'كوشة وديكور قاعة متميز', tier: PackageTier.GOLD, category: PackageCategory.DECORATION, basePrice: 2500, pricePerPerson: 0 },
  ];

  for (const pkg of globalPackages) {
    await prisma.package.create({ data: pkg });
  }

  console.log('Seeding completed successfully!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
