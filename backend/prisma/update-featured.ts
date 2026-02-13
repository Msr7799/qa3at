import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

const featuredVenues = [
    { name: "The Ritz Carlton Bahrain Hotel Spa", luxuryRank: 1 },
    { name: "Four Seasons Hotel Bahrain Bay", luxuryRank: 2 },
    { name: "Jumeirah Gulf of Bahrain Resort & Spa", luxuryRank: 3 }, // Try checking exact match or skip if not found
    { name: "Sofitel Bahrain Zallaq Thalassa Sea Spa", luxuryRank: 4 },
    { name: "The Art Hotelresort", luxuryRank: 5 },
    // Fallback if Jumeirah is missing:
    { name: "Gulf Hotel Bahrain Convention & Spa", luxuryRank: 6 },
];

async function main() {
    console.log('Resetting featured status for all venues...');
    await prisma.venue.updateMany({
        data: {
            isFeatured: false,
            luxuryRank: 999,
        },
    });

    console.log('Setting featured venues...');

    // Process them one by one
    let rankCounter = 1;

    // Priority List (Correct Names from DB)
    const targetNames = [
        "The Ritz Carlton Bahrain Hotel Spa",
        "Four Seasons Hotel Bahrain Bay",
        "Sofitel Bahrain Zallaq Thalassa Sea Spa",
        "The Art Hotelresort",
        "Gulf Hotel Bahrain Convention & Spa", // Replacement for Jumeirah
    ];

    for (const targetName of targetNames) {
        const venue = await prisma.venue.findFirst({
            where: {
                name: {
                    equals: targetName,
                    mode: 'insensitive'
                }
            }
        });

        if (venue) {
            await prisma.venue.update({
                where: { id: venue.id },
                data: {
                    isFeatured: true,
                    luxuryRank: rankCounter++,
                },
            });
            console.log(`Updated: ${venue.name} (Rank: ${rankCounter - 1})`);
        } else {
            console.warn(`Venue not found: ${targetName}`);
            // Try partial search as fallback
            const partialVenue = await prisma.venue.findFirst({
                where: {
                    name: {
                        contains: targetName.split(' ')[0], // First word
                        mode: 'insensitive'
                    }
                }
            });
            if (partialVenue) {
                console.log(`   -> Found similar: ${partialVenue.name}? (Skipping to be safe)`);
            }
        }
    }
}

main()
    .catch((e) => {
        console.error(e);
        process.exit(1);
    })
    .finally(async () => {
        await prisma.$disconnect();
    });
