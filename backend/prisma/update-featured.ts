import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

async function main() {
    // Reset all venues
    await prisma.venue.updateMany({ data: { isFeatured: false, luxuryRank: 999 } });

    // 1. The Ritz-Carlton, Bahrain
    // Using the exact name found: 'The Ritz Carlton Bahrain Hotel Spa'
    const r1 = await prisma.venue.updateMany({
        where: { name: { contains: 'The Ritz Carlton Bahrain Hotel Spa', mode: 'insensitive' } },
        data: { isFeatured: true, luxuryRank: 1 },
    });
    console.log(`Ritz-Carlton: ${r1.count} updated`);

    // 2. Sofitel Bahrain Zallaq
    const r2 = await prisma.venue.updateMany({
        where: { name: { contains: 'Sofitel', mode: 'insensitive' } },
        data: { isFeatured: true, luxuryRank: 2 },
    });
    console.log(`Sofitel: ${r2.count} updated`);

    // 3. ART Rotana Amwaj Islands
    const r3 = await prisma.venue.updateMany({
        where: {
            OR: [
                { name: { contains: 'Art Rotana', mode: 'insensitive' } },
                { name: { contains: 'ART Hotel', mode: 'insensitive' } },
                { name: { contains: 'Art Hotel', mode: 'insensitive' } },
            ]
        },
        data: { isFeatured: true, luxuryRank: 3 },
    });
    console.log(`ART Rotana: ${r3.count} updated`);

    // 4. Four Seasons Hotel Bahrain Bay
    const r4 = await prisma.venue.updateMany({
        where: { name: { contains: 'Four Seasons', mode: 'insensitive' } },
        data: { isFeatured: true, luxuryRank: 4 },
    });
    console.log(`Four Seasons: ${r4.count} updated`);

    // 5. Raffles Al Areen Palace Bahrain
    const r5 = await prisma.venue.updateMany({
        where: { name: { contains: 'Raffles', mode: 'insensitive' } },
        data: { isFeatured: true, luxuryRank: 5 },
    });
    console.log(`Raffles: ${r5.count} updated`);

    // Set luxury ranks for remaining venues based on price (higher price = more luxury)
    const allVenues = await prisma.venue.findMany({
        where: { isFeatured: false },
        orderBy: [{ basePrice: 'desc' }, { pricePerPerson: 'desc' }],
    });

    for (let i = 0; i < allVenues.length; i++) {
        await prisma.venue.update({
            where: { id: allVenues[i].id },
            data: { luxuryRank: 10 + i },
        });
    }
    console.log(`Set luxury rank for ${allVenues.length} other venues`);

    // Verify
    const featured = await prisma.venue.findMany({
        where: { isFeatured: true },
        select: { name: true, luxuryRank: true },
        orderBy: { luxuryRank: 'asc' },
    });
    console.log('\nFeatured venues:', featured);
}

main()
    .catch(console.error)
    .finally(() => prisma.$disconnect());
